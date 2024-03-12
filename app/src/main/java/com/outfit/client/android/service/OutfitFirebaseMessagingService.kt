package com.outfit.client.android.service

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavDeepLinkBuilder
import androidx.navigation.NavGraph
import androidx.work.*
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.outfit.client.android.R
import com.outfit.client.android.data.NotificationType
import com.outfit.client.android.pref.CloudMessagingPref
import com.outfit.client.android.pref.SessionPref
import com.outfit.client.android.ui.viewcomments.ViewCommentsFragmentArgs
import com.outfit.client.android.ui.viewfollow.ViewFollowFragmentArgs
import com.outfit.client.android.ui.viewshot.ViewShotFragmentArgs
import com.outfit.client.android.work.UploadPushTokenWorker
import dagger.android.AndroidInjection
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class OutfitFirebaseMessagingService : FirebaseMessagingService() {

	companion object {
		const val ACTION_RECEIVE_NOTIFICATION =
			"com.outfit.client.android.service.OutfitFirebaseMessagingService.ACTION_RECEIVE_NOTIFICATION"
		const val KEY_TYPE =
			"com.outfit.client.android.service.OutfitFirebaseMessagingService.KEY_TYPE"
		const val KEY_SHOT_ID =
			"com.outfit.client.android.service.OutfitFirebaseMessagingService.KEY_SHOT_ID"
		const val KEY_COMMENT_ID =
			"com.outfit.client.android.service.OutfitFirebaseMessagingService.KEY_COMMENT_ID"

		fun parseDeepLinkIntent(context: Context, graph: NavGraph, bundle: Bundle): Intent? {
			val notificationType = bundle.getString("type")?.let {
				try {
					NotificationType.valueOf(it)
				} catch (e: Throwable) {
					null
				}
			} ?: return null

			val shotId = bundle.getLong("shotId")
			val commentId = bundle.getLong("commentId")


			val destinationId: Int
			val navArgs: Bundle

			when (notificationType) {
				NotificationType.ShotPosted,
				NotificationType.ShotIncludePersonTag,
				NotificationType.ShotLiked -> {
					destinationId = R.id.viewShotFragment
					navArgs = ViewShotFragmentArgs(shotId, null).toBundle()
				}
				NotificationType.Commented,
				NotificationType.CommentIncludePersonTag,
				NotificationType.CommentLiked -> {
					destinationId = R.id.viewCommentsFragment
					navArgs = ViewCommentsFragmentArgs(shotId, commentId, true).toBundle()
				}
				NotificationType.Followed -> {
					destinationId = R.id.viewFollowFragment
					navArgs = ViewFollowFragmentArgs(SessionPref.id, false).toBundle()
				}
			}
			val createTaskStackBuilder = NavDeepLinkBuilder(context)
				.setGraph(graph)
				.setDestination(destinationId)
				.setArguments(navArgs)
				.createTaskStackBuilder()

			return createTaskStackBuilder.lastOrNull()
		}
	}

	@Inject
	lateinit var notificationService: NotificationService

	override fun onCreate() {
		AndroidInjection.inject(this)
	}

	override fun onNewToken(newToken: String) {
		val exToken = CloudMessagingPref.token
		if (exToken != null)
			CloudMessagingPref.expiredTokens.add(exToken)
		CloudMessagingPref.token = newToken
		WorkManager.getInstance(this)
			.enqueueUniqueWork(
				UploadPushTokenWorker::class.java.simpleName,
				ExistingWorkPolicy.REPLACE,
				OneTimeWorkRequestBuilder<UploadPushTokenWorker>()
					.setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.MINUTES)
					.setInputData(workDataOf(UploadPushTokenWorker.KEY_TOKEN to newToken))
					.setConstraints(
						Constraints.Builder()
							.setRequiredNetworkType(NetworkType.CONNECTED)
							.build()
					)
					.build()
			)
	}

	override fun onMessageReceived(remoteMessage: RemoteMessage) {
		Timber.d("onMessageReceived -> ${remoteMessage.data}")
		val type = remoteMessage.data["type"]?.let {
			try {
				NotificationType.valueOf(it)
			} catch (e: Throwable) {
				null
			}
		} ?: return
		val shotId = remoteMessage.data["shotId"]?.toLongOrNull()
		val commentId = remoteMessage.data["commentId"]?.toLongOrNull()

		LocalBroadcastManager.getInstance(this)
			.sendBroadcast(Intent(ACTION_RECEIVE_NOTIFICATION).apply {
				putExtra(KEY_TYPE, type)
				putExtra(KEY_SHOT_ID, shotId)
				putExtra(KEY_COMMENT_ID, commentId)
			})
	}
}