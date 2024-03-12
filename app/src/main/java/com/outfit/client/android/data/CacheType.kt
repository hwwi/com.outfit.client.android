package com.outfit.client.android.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
enum class CacheType : Parcelable {
	EXPLORE,
	CLOSET,
	SUBSCRIPTIONS,
	BOOKMARKS
}