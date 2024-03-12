package com.outfit.client.android.pref

import com.chibatching.kotpref.KotprefModel
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase

object SessionPref : KotprefModel() {
	private var _id by longPref(key = "id")
	var id: Long
		get() = _id
		set(value) {
			setFirebaseUserId(value)
			_id = value
		}

	var phoneOrEmailOrName by nullableStringPref()
	var accessToken by nullableStringPref()
	var refreshToken by nullableStringPref()

	var lastSignInPhoneOrEmailOrName by nullableStringPref()

	fun logout() {
		setFirebaseUserId(null)
		val tempLastSignInEmail = lastSignInPhoneOrEmailOrName
		clear()
		lastSignInPhoneOrEmailOrName = tempLastSignInEmail
	}

	private fun setFirebaseUserId(userId: Long? = null) {
		val strUserId = userId?.toString()
		FirebaseCrashlytics.getInstance().setUserId(strUserId ?: "")
		Firebase.analytics.setUserId(strUserId)
	}
}