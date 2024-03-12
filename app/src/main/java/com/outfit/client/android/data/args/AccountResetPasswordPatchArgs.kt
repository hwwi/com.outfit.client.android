package com.outfit.client.android.data.args

data class AccountResetPasswordPatchArgs(
	val verificationId: Long,
	val newPassword: String,
	val ConfirmPassword: String
)