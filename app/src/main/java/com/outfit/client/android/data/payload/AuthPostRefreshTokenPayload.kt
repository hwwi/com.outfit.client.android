package com.outfit.client.android.data.payload

data class AuthPostRefreshTokenPayload(
	val accessToken: String,
	val refreshToken: String
)