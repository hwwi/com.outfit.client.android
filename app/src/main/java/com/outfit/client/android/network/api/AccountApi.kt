package com.outfit.client.android.network.api

import com.outfit.client.android.data.NetworkState
import com.outfit.client.android.data.args.*
import com.outfit.client.android.data.payload.*
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import retrofit2.http.*

interface AccountApi {
	@PUT("account/logout")
	suspend fun putLogout(
		@Body body: AccountPutLogoutArgs
	)

	@PUT("account")
	fun put(
		@Body args: AccountPutArgs
	): Flow<NetworkState<AccountPutPayload>>

	@PATCH("account/name")
	fun patchName(
		@Body args: AccountPatchNameArgs
	): Flow<NetworkState<AccountPatchNamePayload>>

	@PATCH("account/password")
	fun patchPassword(
		@Body args: AccountPasswordPatchArgs
	): Flow<NetworkState<Unit>>

	@PATCH("account/reset/password")
	fun patchResetPassword(
		@Body args: AccountResetPasswordPatchArgs
	): Flow<NetworkState<Unit>>

	@Multipart
	@PATCH("account/profileImage")
	suspend fun patchProfileImage(
		@Part file: MultipartBody.Part
	): AccountPatchProfileImagePayload

	@Multipart
	@PATCH("account/closetBackgroundImage")
	suspend fun patchClosetBackgroundImage(
		@Part file: MultipartBody.Part
	): AccountPatchClosetBackgroundImagePayload

	@DELETE("account/profileImage")
	suspend fun deleteProfileImage(): AccountDeleteProfileImagePayload

	@DELETE("account/closetBackgroundImage")
	suspend fun deleteClosetBackgroundImage(): AccountDeleteClosetBackgroundImagePayload

}