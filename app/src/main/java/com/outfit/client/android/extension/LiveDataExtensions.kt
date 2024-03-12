package com.outfit.client.android.extension

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.map
import com.outfit.client.android.data.NetworkState

fun <X, Y> LiveData<X>.mapNotNull(
	transform: (X) -> Y?
): LiveData<Y> {
	val result = MediatorLiveData<Y>()
	result.addSource(
		this
	) { x ->
		val y = transform(x)
		if (y != null)
			result.value = y
	}
	return result
}

fun <T> LiveData<NetworkState<T>>.onSuccess(onSuccess: ((T) -> Unit)? = null): LiveData<NetworkState<T>> =
	map {
		when (it) {
			is NetworkState.Success -> {
				onSuccess?.invoke(it.data)
				it
			}
			else -> it
		}
	}


fun <T> LiveData<NetworkState<T>>.filterData(): LiveData<T> =
	mapNotNull {
		when {
			it is NetworkState.Fetching -> it.data
			it is NetworkState.Success -> it.data
			else -> null
		}
	}

fun <T> LiveData<NetworkState<T>>.ignoreData(): LiveData<NetworkState<Unit>> =
	map {
		when (it) {
			is NetworkState.Fetching -> it as NetworkState.Fetching<Unit>
			is NetworkState.Fail -> it as NetworkState.Fail<Unit>
			is NetworkState.Success -> NetworkState.success(Unit)
		}
	}