package com.outfit.client.android.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.outfit.client.android.data.args.NotificationPostCloudMessagingTokenArgs
import com.outfit.client.android.di.ChildWorkerFactory
import com.outfit.client.android.network.api.NotificationApi
import com.outfit.client.android.pref.CloudMessagingPref
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import timber.log.Timber

class UploadPushTokenWorker @AssistedInject constructor(
	@Assisted context: Context,
	@Assisted workerParams: WorkerParameters,
	private val notificationApi: NotificationApi
) : CoroutineWorker(context, workerParams) {
	companion object {
		const val KEY_TOKEN =
			"com.outfit.client.android.work.UploadPushTokenWorker.KEY_TOKEN"
	}

	@AssistedInject.Factory
	interface Factory : ChildWorkerFactory

	override suspend fun doWork(): Result {
		val token = inputData.getString(KEY_TOKEN) ?: return Result.failure()
		val expiredFirebaseTokens = CloudMessagingPref.expiredTokens.toList()
		try {
			notificationApi.postCloudMessagingToken(
				NotificationPostCloudMessagingTokenArgs(
					currentToken = token,
					expiredTokens = expiredFirebaseTokens
				)
			)
			CloudMessagingPref.expiredTokens.removeAll(expiredFirebaseTokens)
		} catch (e: Throwable) {
			Timber.e(e)
			return Result.retry()
		}
		return Result.success()
	}
}