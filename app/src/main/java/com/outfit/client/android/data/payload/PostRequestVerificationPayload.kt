package com.outfit.client.android.data.payload

import com.outfit.client.android.data.VerificationMethod
import com.outfit.client.android.data.VerificationPurpose

data class PostRequestVerificationPayload(
	val verificationId: Long,
	val purpose: VerificationPurpose,
	val method: VerificationMethod,
	val codeLength : Int
)