package com.outfit.client.android.network.api

import com.outfit.client.android.data.AnonymousVerificationPurpose
import com.outfit.client.android.data.NetworkState
import com.outfit.client.android.data.VerificationMethod
import com.outfit.client.android.data.VerificationPurpose
import com.outfit.client.android.data.payload.PostRequestAnonymousVerificationPayload
import com.outfit.client.android.data.payload.PostRequestVerificationPayload
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface VerificationApi {
	@POST("verification/{purpose}/{method}")
	fun postRequestVerification(
		@Path("purpose") purpose: VerificationPurpose,
		@Path("method") method: VerificationMethod
	): Flow<NetworkState<PostRequestVerificationPayload>>

	@POST("verification/{purpose}/sms/{number}/{region}")
	fun postRequestAnonymousSmsVerification(
		@Path("purpose") purpose: AnonymousVerificationPurpose,
		@Path("number") number: String,
		@Path("region") region: String
	): Flow<NetworkState<PostRequestAnonymousVerificationPayload>>

	@POST("verification/{purpose}/email/{email}")
	fun postRequestAnonymousEmailVerification(
		@Path("purpose") purpose: AnonymousVerificationPurpose,
		@Path("email") email: String
	): Flow<NetworkState<PostRequestAnonymousVerificationPayload>>

	@GET("verification/{verificationId}/newCode")
	fun getRequestNewCode(
		@Path("verificationId") verificationId: Long
	): Flow<NetworkState<Unit>>

	@GET("verification/{verificationId}/{verificationCode}")
	fun getVerifyCode(
		@Path("verificationId") verificationId: Long,
		@Path("verificationCode") verificationCode: String
	): Flow<NetworkState<Unit>>
}