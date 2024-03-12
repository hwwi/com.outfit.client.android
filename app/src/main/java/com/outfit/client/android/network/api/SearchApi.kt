package com.outfit.client.android.network.api

import com.outfit.client.android.data.NetworkState
import com.outfit.client.android.data.payload.SearchGetPayload
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApi {

	@GET("search")
	fun getSearch(@Query("query") query: String): Flow<NetworkState<SearchGetPayload>>
}