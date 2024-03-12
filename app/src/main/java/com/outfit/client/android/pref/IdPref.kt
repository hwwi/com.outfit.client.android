package com.outfit.client.android.pref

import com.chibatching.kotpref.KotprefModel
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase
import java.util.*

object IdPref : KotprefModel() {
	var appUuid by stringPref()
		private set

	init {
		if (appUuid.isEmpty()) {
			val uuid = UUID.randomUUID().toString()
			appUuid = uuid
			FirebaseCrashlytics.getInstance().setCustomKey("appUuid", uuid)
			Firebase.analytics.setUserProperty("appUuid", uuid)
		}
	}
}