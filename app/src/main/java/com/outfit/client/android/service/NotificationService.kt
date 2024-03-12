package com.outfit.client.android.service

import android.app.Notification
import android.app.NotificationChannel
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import javax.inject.Inject

class NotificationService @Inject constructor(
	private val context: Context
) {
	fun notificationBuilder(
		channelType: NotificationChannelType,
		dsl: (NotificationCompat.Builder.() -> Unit)?
	): NotificationCompat.Builder {
		val channelId = channelType.getId(context)
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val channel = NotificationChannel(
				channelId,
				channelType.getName(context),
				channelType.important
			).apply {
				description = channelType.getDescription(context)
			}
			NotificationManagerCompat
				.from(context)
				.createNotificationChannel(channel)
		}
		return NotificationCompat.Builder(context, channelId)
			.apply {
				setWhen(System.currentTimeMillis())
				setSmallIcon(channelType.smallIconDrawableRes)
				dsl?.invoke(this)
				priority = channelType.priority
			}
	}

	fun notification(
		channelType: NotificationChannelType,
		dsl: (NotificationCompat.Builder.() -> Unit)?
	): Notification = notificationBuilder(channelType, dsl).build()

	fun notify(channelType: NotificationChannelType, dsl: NotificationCompat.Builder.() -> Unit) {
		NotificationManagerCompat
			.from(context)
			.notify(channelType.generateNotificationId(), notification(channelType, dsl))
	}
}

