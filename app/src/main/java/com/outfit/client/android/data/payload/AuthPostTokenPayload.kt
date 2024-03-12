package com.outfit.client.android.data.payload

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class AuthPostTokenPayload(
	var id: Long,
	var phoneOrEmailOrName: String,
	var accessToken: String,
	val refreshToken: String
) : Parcelable