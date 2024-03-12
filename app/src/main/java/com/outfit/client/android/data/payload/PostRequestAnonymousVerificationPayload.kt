package com.outfit.client.android.data.payload

import android.os.Parcelable
import com.outfit.client.android.data.AnonymousVerificationPurpose
import com.outfit.client.android.data.VerificationMethod
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PostRequestAnonymousVerificationPayload(
	val verificationId: Long,
	val purpose: AnonymousVerificationPurpose,
	val method: VerificationMethod,
	val to: String,
	val codeLength: Int
) : Parcelable