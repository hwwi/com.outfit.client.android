package com.outfit.client.android.pref

import com.chibatching.kotpref.KotprefModel

object IntroducePref : KotprefModel() {
	var lastId by longPref()

	var isIntroduceSearchFragment by booleanPref()
//	var isIntroduceExploreFragment by booleanPref()
//	var isIntroduceSubscriptionsFragment by booleanPref()
//	var isIntroduceNotificationFragment by booleanPref()

	var isWarningPostConfirmFragmentPostPolicy by booleanPref()
}