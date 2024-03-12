package com.outfit.client.android.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import androidx.core.net.ConnectivityManagerCompat
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.bytesDownloaded
import com.google.android.play.core.ktx.installStatus
import com.google.android.play.core.ktx.totalBytesToDownload
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.outfit.client.android.BuildConfig
import com.outfit.client.android.MainNavDirections
import com.outfit.client.android.R
import com.outfit.client.android.data.NotificationType
import com.outfit.client.android.data.SemVer
import com.outfit.client.android.databinding.ActivityNavBinding
import com.outfit.client.android.db.NetworkCacheDatabase
import com.outfit.client.android.di.DaggerFragmentFactory
import com.outfit.client.android.di.DaggerViewModelProviderFactoryBuilder
import com.outfit.client.android.di.module.NetworkModule
import com.outfit.client.android.extension.showError
import com.outfit.client.android.extension.toGoneOrVisible
import com.outfit.client.android.extension.toVisibleOrGone
import com.outfit.client.android.pref.CloudMessagingPref
import com.outfit.client.android.pref.SessionPref
import com.outfit.client.android.repository.AccountRepository
import com.outfit.client.android.service.OutfitFirebaseMessagingService
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class NavActivity : AppCompatActivity(), HasAndroidInjector {
	companion object {
		const val REQUEST_CODE_APP_UPDATE_IMMEDIATE = 1
		const val REQUEST_CODE_APP_UPDATE_FLEXIBLE = 2
		const val ACTION_NAVIGATION_ITEM_RESELECTED_AND_BADGE_CLEARED =
			"com.outfit.client.android.ui.NavActivity.ACTION_NAVIGATION_ITEM_RESELECTED_AND_BADGE_CLEARED"
		const val KEY_ITEM_ID =
			"com.outfit.client.android.ui.NavActivity.KEY_ITEM_ID"

		fun createAppBarConfiguration(): AppBarConfiguration = AppBarConfiguration(
			setOf(
				R.id.exploreFragment,
				R.id.subscriptionsFragment,
				R.id.notificationsFragment,
				R.id.myAccountFragment
			)
		)
	}

	@Inject
	lateinit var fragmentFactory: DaggerFragmentFactory

	@Inject
	lateinit var viewModelProviderFactoryBuilder: DaggerViewModelProviderFactoryBuilder

	@Inject
	lateinit var androidInjector: DispatchingAndroidInjector<Any>

	@Inject
	lateinit var accountRepository: AccountRepository

	@Inject
	lateinit var networkCacheDatabase: NetworkCacheDatabase

	@Inject
	lateinit var firebaseAnalytics: FirebaseAnalytics


	private val binding: ActivityNavBinding by lazy { ActivityNavBinding.inflate(layoutInflater) }

	override fun androidInjector(): AndroidInjector<Any> = androidInjector

	private val connectivityBroadcastReceiver = object : BroadcastReceiver() {
		override fun onReceive(context: Context?, intent: Intent?) {
			if (intent?.action != ConnectivityManager.CONNECTIVITY_ACTION)
				return
			binding.noInternetConnection.visibility =
				intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)
					.toVisibleOrGone()
		}
	}
	private val localBroadcastReceiver = object : BroadcastReceiver() {
		private var logoutDialog: AlertDialog? = null
		override fun onReceive(context: Context, intent: Intent) {
			when (intent.action) {
				NetworkModule.ACTION_SESSION_EXPIRED -> {
					if (logoutDialog?.isShowing == true)
						return
					logoutDialog = MaterialAlertDialogBuilder(this@NavActivity)
						.setMessage(getString(R.string.msg_your_session_has_expired_please_login_again))
						.setCancelable(false)
						.setPositiveButton(R.string.str_ok, null)
						.create()
						.apply {
							setOnShowListener { dialog ->
								getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
									lifecycleScope.launch {
										try {
											logout()
										} catch (e: Exception) {
											showError(e)
										}
										dialog.dismiss()
									}
								}
							}
							show()
						}
				}
				OutfitFirebaseMessagingService.ACTION_RECEIVE_NOTIFICATION -> {
					val notificationType = intent
						.getSerializableExtra(OutfitFirebaseMessagingService.KEY_TYPE) as? NotificationType
						?: return

					val badge = binding.bottomNavigation.getOrCreateBadge(
						when (notificationType) {
							NotificationType.ShotPosted -> R.id.subscriptionsFragment
							else -> R.id.notificationsFragment
						}
					)
					badge.number += 1
					badge.isVisible = true
				}
			}
		}
	}

	private val appUpdateManager: AppUpdateManager by lazy {
		AppUpdateManagerFactory.create(this).apply {
			registerListener {
				when (val installState = it.installStatus()) {
					InstallStatus.CANCELED,
					InstallStatus.FAILED,
					InstallStatus.INSTALLED,
					InstallStatus.INSTALLING,
					InstallStatus.PENDING,
					InstallStatus.UNKNOWN,
					InstallStatus.REQUIRES_UI_INTENT -> {
						Timber.d("installState : $installState")
					}
					InstallStatus.DOWNLOADING -> {
						if (binding.downloadProgress.visibility != View.VISIBLE)
							binding.downloadProgress.visibility = View.VISIBLE
						val bytesDownloaded = it.bytesDownloaded
						val totalBytesToDownload = it.totalBytesToDownload
						binding.downloadProgress.progress =
							(bytesDownloaded * 100.0 / totalBytesToDownload).toInt()
					}
					InstallStatus.DOWNLOADED -> {
						binding.downloadProgress.visibility = View.GONE
						popupSnackbarForCompleteUpdate()
					}
				}
			}
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		AndroidInjection.inject(this)
		supportFragmentManager.fragmentFactory = fragmentFactory
		super.onCreate(savedInstanceState)
		LocalBroadcastManager.getInstance(this)
			.registerReceiver(localBroadcastReceiver, IntentFilter().apply {
				addAction(NetworkModule.ACTION_SESSION_EXPIRED)
				addAction(OutfitFirebaseMessagingService.ACTION_RECEIVE_NOTIFICATION)
			})
		setContentView(binding.root)
		findNavController().apply {
			binding.bottomNavigation.setupWithNavController(this)
			binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
				when (item.itemId) {
					R.id.subscriptionsFragment,
					R.id.notificationsFragment -> clearBadge(item.itemId)
				}
				NavigationUI.onNavDestinationSelected(item, this)
			}
			binding.bottomNavigation.setOnNavigationItemReselectedListener { item ->
				when (item.itemId) {
					R.id.subscriptionsFragment,
					R.id.notificationsFragment -> {
						if (clearBadge(item.itemId)) {
							LocalBroadcastManager.getInstance(this@NavActivity)
								.sendBroadcast(
									Intent(
										ACTION_NAVIGATION_ITEM_RESELECTED_AND_BADGE_CLEARED
									).apply {
										putExtra(KEY_ITEM_ID, item.itemId)
									}
								)
						}
					}
				}
			}
			val graph = navInflater.inflate(R.navigation.nav_graph)
			val deepLinkIntent = intent.extras?.let {
				OutfitFirebaseMessagingService.parseDeepLinkIntent(this@NavActivity, graph, it)
			}
			if (deepLinkIntent != null)
				intent = deepLinkIntent
			setGraph(graph)

			addOnDestinationChangedListener { _, destination, _ ->
				val destinationName = try {
					resources.getResourceName(destination.id)
						.substringAfter(":id/")
						.capitalize(Locale.ROOT)
				} catch (e: Throwable) {
					destination.id.toString()
				}
				firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
					param(FirebaseAnalytics.Param.SCREEN_CLASS, destinationName)
				}

				when (destination.id) {
					R.id.postCommentDialog,
					R.id.modifyItemTagDialog,
					R.id.deleteShotConfirmDialog,
					R.id.deleteCommentConfirmDialog,
					R.id.changeViewerProfileImageDialog,
					R.id.deleteViewerProfileImageDialog,
					R.id.changeViewerClosetBackgroundImageDialog,
					R.id.deleteViewerClosetBackgroundImageDialog -> {
					}
					else -> binding.bottomNavigation.visibility = when (destination.parent?.id) {
						R.id.post_navigation,
						R.id.anonymous_verification_navigation,
						R.id.nav_authentication,
						R.id.selectImageNav -> View.GONE
						else -> when (destination.id) {
							R.id.searchFragment -> View.GONE
							else -> View.VISIBLE
						}
					}
				}
			}
		}

		appUpdateManager
			.appUpdateInfo
			.addOnSuccessListener { appUpdateInfo ->
				if (appUpdateInfo.updateAvailability() != UpdateAvailability.UPDATE_AVAILABLE) {
					// TODO 업데이트 채크 바로 안되서 확인용, 추후 삭제
					FirebaseCrashlytics.getInstance()
						.recordException(IllegalStateException("App update checked but cant update -> $appUpdateInfo"))
					return@addOnSuccessListener
				}

				val semVer = SemVer(BuildConfig.VERSION_CODE)
				val availableSemVer = SemVer(appUpdateInfo.availableVersionCode())
				when {
					semVer.major != availableSemVer.major ->
						startUpdate(appUpdateInfo, AppUpdateType.IMMEDIATE)
					semVer.minor != availableSemVer.minor ->
						startUpdate(appUpdateInfo, AppUpdateType.FLEXIBLE)
					semVer.patch != availableSemVer.patch -> {
						// 초기에 업데이트 빈번하므로 안정화 될때까진 바로 띄움.
						//				val clientVersionStalenessDays = appUpdateInfo.clientVersionStalenessDays
						//				if (clientVersionStalenessDays == null || clientVersionStalenessDays >= 3)
						startUpdate(appUpdateInfo, AppUpdateType.FLEXIBLE)
					}
				}
			}
			.addOnFailureListener {
				FirebaseCrashlytics.getInstance()
					// 추후 안정화 되면 로깅으로 변경
					.recordException(it)
//					.log("App update check fail -> ${it.javaClass.simpleName}: ${it.message ?: "null"}")
			}
	}

	override fun onNewIntent(intent: Intent?) {
		super.onNewIntent(intent)
		findNavController().handleDeepLink(intent)
	}

	private fun startUpdate(appUpdateInfo: AppUpdateInfo, appUpdateType: Int) {
		appUpdateManager.startUpdateFlowForResult(
			appUpdateInfo,
			appUpdateType,
			this,
			when (appUpdateType) {
				AppUpdateType.IMMEDIATE -> REQUEST_CODE_APP_UPDATE_IMMEDIATE
				AppUpdateType.FLEXIBLE -> REQUEST_CODE_APP_UPDATE_FLEXIBLE
				else -> throw UnsupportedOperationException()
			}
		)
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		when (requestCode) {
			REQUEST_CODE_APP_UPDATE_IMMEDIATE -> {
				FirebaseCrashlytics.getInstance()
					.log("REQUEST_CODE_APP_UPDATE_IMMEDIATE -> resultCode: $resultCode")
//				when (resultCode) {
//					RESULT_CANCELED -> {
//					}
//					ActivityResult.RESULT_IN_APP_UPDATE_FAILED -> {
//					}
//				}
			}
			REQUEST_CODE_APP_UPDATE_FLEXIBLE -> {
				FirebaseCrashlytics.getInstance()
					.log("REQUEST_CODE_APP_UPDATE_FLEXIBLE -> resultCode: $resultCode")
			}
			else -> super.onActivityResult(requestCode, resultCode, data)
		}
	}

	override fun onResume() {
		super.onResume()
		appUpdateManager
			.appUpdateInfo
			.addOnSuccessListener { appUpdateInfo ->
				// The API does not return whether the update was started as an immediate or flexible flow.
				// So re-run the same logic that you ran to decide what type to start initially
				// https://stackoverflow.com/questions/56365502/how-to-know-the-appupdatetype-at-the-origin-of-the-developer-triggered-update-in
				// https://issuetracker.google.com/issues/153785560
				val semVer = SemVer(BuildConfig.VERSION_CODE)
				val availableSemVer = SemVer(appUpdateInfo.availableVersionCode())
				when {
					// AppUpdateType.IMMEDIATE, If an in-app update is already running, resume the update.
					semVer.major != availableSemVer.major ->
						if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS)
							startUpdate(appUpdateInfo, AppUpdateType.IMMEDIATE)
					// AppUpdateType.FLEXIBLE, If the update is downloaded but not installed, notify the user to complete the update.
					semVer.minor != availableSemVer.minor || semVer.patch != availableSemVer.patch ->
						if (appUpdateInfo.installStatus == InstallStatus.DOWNLOADED) {
							binding.downloadProgress.hide()
							popupSnackbarForCompleteUpdate()
						}
				}
			}
		registerReceiver(
			connectivityBroadcastReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
		)
	}

	override fun onPause() {
		super.onPause()
		unregisterReceiver(connectivityBroadcastReceiver)
	}

	override fun onDestroy() {
		super.onDestroy()
		LocalBroadcastManager.getInstance(this)
			.unregisterReceiver(localBroadcastReceiver)
	}

	private fun popupSnackbarForCompleteUpdate() {
		Snackbar.make(
			binding.bottomNavigation,
			R.string.msg_an_update_has_just_been_downloaded,
			Snackbar.LENGTH_INDEFINITE
		).apply {
			setAction(R.string.str_restart) { appUpdateManager.completeUpdate() }
//			setActionTextColor(resources.getColor(R.color.snackbar_action_text_color))
			show()
		}
	}

	suspend fun logout() {
		CloudMessagingPref.token?.let {
			accountRepository.logout(it)
		}
		withContext(Dispatchers.IO) {
			networkCacheDatabase.clearAllTables()
		}
		SessionPref.logout()
		viewModelStore.clear()
		MainNavDirections.actionToMainNav().navigate()
	}

	private fun clearBadge(itemId: Int): Boolean {
		val badge = binding.bottomNavigation.getBadge(itemId) ?: return false
		if (!badge.isVisible) return false
		badge.isVisible = false
		badge.clearNumber()
		return true
	}

	// https://issuetracker.google.com/issues/142847973
	fun findNavController(): NavController =
		(supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment).navController

	fun NavDirections.navigate(navOptions: NavOptions? = null) {
		findNavController().navigate(this, navOptions)
	}
}
