package com.outfit.client.android.data.args

data class PersonPostArgs(
	var email: String?,
	var phoneNumber: String?,
	var verificationId: Long,
	var name: String,
	var password: String,
	val confirmPassword: String
)
