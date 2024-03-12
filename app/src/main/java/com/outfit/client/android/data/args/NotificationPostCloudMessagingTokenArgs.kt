package com.outfit.client.android.data.args

data class NotificationPostCloudMessagingTokenArgs(
	val currentToken: String?,
	val expiredTokens: List<String>?
)