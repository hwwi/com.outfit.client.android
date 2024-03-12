package com.outfit.client.android.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PopUpTo(
	val destinationId: Int,
	val inclusive: Boolean
) : Parcelable