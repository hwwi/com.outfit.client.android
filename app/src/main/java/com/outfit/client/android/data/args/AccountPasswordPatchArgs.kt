package com.outfit.client.android.data.args

data class AccountPasswordPatchArgs(
	val CurrentPassword: String,
	val newPassword: String,
	val ConfirmPassword: String
)