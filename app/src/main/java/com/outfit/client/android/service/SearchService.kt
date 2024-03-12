package com.outfit.client.android.service

import com.outfit.client.android.data.NetworkState
import com.outfit.client.android.data.payload.SearchGetPayload
import com.outfit.client.android.network.api.SearchApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchService @Inject constructor(
	private val searchApi: SearchApi
) {

	fun loadSearch(query: String): Flow<NetworkState<SearchGetPayload>> =
		searchApi.getSearch(query)
}