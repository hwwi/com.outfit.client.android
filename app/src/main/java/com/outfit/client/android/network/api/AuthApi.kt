package com.outfit.client.android.network.api

import com.outfit.client.android.data.NetworkState
import com.outfit.client.android.data.args.AuthPostRefreshTokenArgs
import com.outfit.client.android.data.args.AuthPostTokenArgs
import com.outfit.client.android.data.payload.AuthPostRefreshTokenPayload
import com.outfit.client.android.data.payload.AuthPostTokenPayload
import kotlinx.coroutines.flow.Flow
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
	@POST("authentication/token")
	fun postToken(@Body args: AuthPostTokenArgs): Flow<NetworkState<AuthPostTokenPayload>>

	@POST("authentication/refreshToken")
	fun postRefreshToken(@Body args: AuthPostRefreshTokenArgs): Call<AuthPostRefreshTokenPayload>
}