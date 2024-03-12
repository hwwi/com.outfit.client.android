package com.outfit.client.android.pref

import com.chibatching.kotpref.KotprefModel

object CloudMessagingPref : KotprefModel() {
	var token by nullableStringPref()
	val expiredTokens by stringSetPref()
}