package com.outfit.client.android.util


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingRequestHelper
import com.outfit.client.android.data.NetworkState

fun PagingRequestHelper.createStatusLiveData(): LiveData<NetworkState<Unit>> {
	val liveData = MutableLiveData<NetworkState<Unit>>()
	addListener { report ->
		liveData.postValue(
			when {
				report.hasRunning() -> NetworkState.Fetching.create()
				report.hasError() -> {
					val error = PagingRequestHelper.RequestType.values()
						.mapNotNull {
							report.getErrorFor(it)
						}
						.first()
					NetworkState.Fail.create(error)
				}
				else -> NetworkState.Success.create(Unit)
			}
		)
	}
	return liveData
}
