package com.outfit.client.android.service

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.outfit.client.android.R

enum class NotificationChannelType {
	Default
	;

	// Notification 의 Id -> 알림마다 고유한 Id, 같은 Id 면 대체됨
	fun generateNotificationId(): Int = when (this) {
		Default -> 1
	}

	// NotificationChannel 의 Id -> 채널마다 고유한 값
	fun getId(context: Context): String =
		when (this) {
			Default -> context.getString(R.string.chanel_id_default)
		}

	// NotificationChannel 의 Name -> 채널마다 사용자에게 보여지는 이름
	fun getName(context: Context): String = context.getString(
		when (this) {
			else -> R.string.app_name
		}
	)

	// NotificationChannel 의 Description -> 채널마다 사용자에게 보여지는 설명
	fun getDescription(context: Context): String = context.getString(
		when (this) {
			else -> R.string.app_name
		}
	)


	// NotificationChannel 의 important -> 채널의 중요도
	// 중요도에 따라 알림음 여부, 헤드업, 상태표시줄 표시 여부 등 달라짐
	// priority 는 Notification 개개인의 세팅이였으나 NotificationChannel 부터는 채널의 important 를 사용하게됨
	// https://developer.android.com/training/notify-user/channels?hl=ko#importance
	@get:RequiresApi(Build.VERSION_CODES.N)
	val important: Int
		get() = when (this) {
			Default -> NotificationManager.IMPORTANCE_LOW
		}

	// Notification 의 priority -> 알림의 중요도
	val priority
		get() = when (important) {
			NotificationManager.IMPORTANCE_MAX -> Notification.PRIORITY_MAX
			NotificationManager.IMPORTANCE_HIGH -> Notification.PRIORITY_HIGH
			NotificationManager.IMPORTANCE_DEFAULT -> Notification.PRIORITY_DEFAULT
			NotificationManager.IMPORTANCE_LOW -> Notification.PRIORITY_LOW
			NotificationManager.IMPORTANCE_MIN -> Notification.PRIORITY_MIN
			else -> Notification.PRIORITY_MIN
		}


	// Notification 의 smallIcon 리소스
	val smallIconDrawableRes: Int
		get() = when (this) {
			else -> R.mipmap.ic_launcher
		}
}