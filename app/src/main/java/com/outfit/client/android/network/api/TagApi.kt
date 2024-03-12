package com.outfit.client.android.network.api

import com.outfit.client.android.data.NetworkState
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Query

interface TagApi {
	@GET("tags/validate/item")
	fun getValidateItem(
		@Query("brandCode") brandCode: String? = null,
		@Query("productCode") productCode: String? = null
	): Flow<NetworkState<Unit>>
}