package com.outfit.client.android.data.args

data class AuthPostTokenArgs(
	val phoneOrEmailOrName: String,
	val password: String,
	val region: String?,
	val cloudMessagingTokens: NotificationPostCloudMessagingTokenArgs?
)