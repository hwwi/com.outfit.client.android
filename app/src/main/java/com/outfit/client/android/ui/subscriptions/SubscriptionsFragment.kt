package com.outfit.client.android.ui.subscriptions

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.outfit.client.android.R
import com.outfit.client.android.data.NetworkState
import com.outfit.client.android.databinding.FragmentSubscriptionsBinding
import com.outfit.client.android.extension.showError
import com.outfit.client.android.pref.SessionPref
import com.outfit.client.android.repository.ShotRepository
import com.outfit.client.android.text.style.OnTagSpansClickListener
import com.outfit.client.android.ui.NavActivity
import com.outfit.client.android.util.PopupMenus
import com.outfit.client.android.widget.DataBindingFragment
import com.outfit.client.android.widget.adapter.LoadingStateInterceptor
import com.outfit.client.android.widget.controller.ShotTimelinePagedController
import kotlinx.coroutines.launch
import javax.inject.Inject


class SubscriptionsFragment @Inject constructor(
	private val shotRepository: ShotRepository
) : DataBindingFragment<FragmentSubscriptionsBinding>(R.layout.fragment_subscriptions) {
	private val viewModel: SubscriptionsViewModel by activityViewModels { viewModelProviderFactory }

	private val localBroadcastReceiver = object : BroadcastReceiver() {
		override fun onReceive(context: Context?, intent: Intent?) {
			if (intent?.action == NavActivity.ACTION_NAVIGATION_ITEM_RESELECTED_AND_BADGE_CLEARED
				&& intent.getIntExtra(NavActivity.KEY_ITEM_ID, 0) == R.id.subscriptionsFragment
			)
				viewModel.refresh()
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		val controller = ShotTimelinePagedController(SessionPref.id).apply {
			onPersonNameClickListener = {
				SubscriptionsFragmentDirections
					.actionToClosetFragment(it)
					.navigate()
			}
			onMenuButtonClickListener = { view, shot ->
				when {
					shot.person.id == SessionPref.id ->
						PopupMenus.showShotOwner(
							this@SubscriptionsFragment,
							view,
							shot,
							shotRepository
						)
//					else -> PopupMenus.showShot(this@SubscriptionsFragment, view, shot)
				}
			}
			onLikeButtonClickListener = { v, shot ->
				viewLifecycleOwner.lifecycleScope.launch {
					v.isEnabled = false
					try {
						when (shot.isViewerLike) {
							true -> shotRepository.deleteLike(shot.id)
							false -> shotRepository.putLike(shot.id)
						}
					} catch (e: Exception) {
						showError(e)
					} finally {
						v.isEnabled = true
					}
				}
			}
			onCommentButtonClickListener = { view, shot ->
				SubscriptionsFragmentDirections
					.actionSubscriptionsFragmentToViewCommentsFragment(shot.id)
					.navigate()
			}
			onBookmarkButtonClickListener = { view, shot ->
				viewLifecycleOwner.lifecycleScope.launch {
					view.isEnabled = false
					try {
						when {
							shot.isViewerBookmark -> shotRepository.deleteBookmark(shot.id)
							else -> shotRepository.putBookmark(shot.id)
						}
					} catch (e: Exception) {
						showError(e)
					} finally {
						view.isEnabled = true
					}
				}
			}
			onTagClickListener = { v, tag ->
				PopupMenus.showOnTagClick(this@SubscriptionsFragment, v, tag)

			}
			onCaptionTagSpansClickListener = object : OnTagSpansClickListener {
				override fun onPersonTagClick(name: String) {
					SubscriptionsFragmentDirections
						.actionSubscriptionsFragmentToClosetFragment(name)
						.navigate()
				}

				override fun onItemTagClick(brand: String, product: String?) {
					SubscriptionsFragmentDirections
						.actionSubscriptionsFragmentToViewItemTagFragment(brand, product)
						.navigate()
				}

				override fun onHashTagClick(tag: String) {
					SubscriptionsFragmentDirections
						.actionSubscriptionsFragmentToViewHashTagFragment(tag)
						.navigate()
				}
			}
		}
		val loadingStateInterceptor = LoadingStateInterceptor(controller) {
			viewModel.retry()
		}

		binding.listShot.apply {
			setController(controller)
		}

		viewModel.shots.observe(viewLifecycleOwner) {
			controller.submitList(it)
			if (it.isNotEmpty()) {
				binding.error.clearError()
				return@observe
			}
			binding.error.setError(
				R.string.error_title_subscription_is_empty,
				R.string.error_msg_subscription_is_empty
			)
		}

		viewModel.shotsNetworkState.observe(viewLifecycleOwner) {
			val state = it.getContentIfNotHandled() ?: return@observe
			loadingStateInterceptor.loadingState = state
		}

		viewModel.shotsRefreshState.observe(viewLifecycleOwner) {
			val state = it.getContentIfNotHandled() ?: return@observe
			binding.swipeRefresh.isRefreshing = state is NetworkState.Fetching
			when (state) {
				is NetworkState.Fetching -> loadingStateInterceptor.loadingState = null
				is NetworkState.Fail -> showError(state.error)
			}
		}

		binding.swipeRefresh.setOnRefreshListener {
			viewModel.refresh()
		}

//		if (!IntroducePref.isIntroduceSubscriptionsFragment)
//			MaterialAlertDialogBuilder(requireContext())
//				.setMessage(R.string.introduce_subscriptions_fragment)
//				.setPositiveButton(R.string.str_ok) { _, _ ->
//					IntroducePref.isIntroduceSubscriptionsFragment = true
//				}
//				.show()
	}

	override fun onResume() {
		super.onResume()
		LocalBroadcastManager.getInstance(requireContext())
			.registerReceiver(
				localBroadcastReceiver,
				IntentFilter(NavActivity.ACTION_NAVIGATION_ITEM_RESELECTED_AND_BADGE_CLEARED)
			)
	}

	override fun onPause() {
		super.onPause()
		LocalBroadcastManager.getInstance(requireContext())
			.unregisterReceiver(localBroadcastReceiver)
	}
}
