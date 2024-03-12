package com.outfit.client.android.extension

import com.outfit.client.android.data.NetworkState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.transform

suspend fun <T> Flow<NetworkState<T>>.last(): NetworkState<T> = toList().last()

fun <T> Flow<NetworkState<T>>.onSuccess(onSuccess: (suspend (T) -> Unit)? = null): Flow<NetworkState<T>> =
	map {
		when (it) {
			is NetworkState.Success -> {
				onSuccess?.invoke(it.data)
				it
			}
			else -> it
		}
	}

fun <T> Flow<NetworkState<T>>.filterData(): Flow<T> =
	transform {
		if (it is NetworkState.Success)
			emit(it.data)
	}

fun <T> Flow<NetworkState<T>>.ignoreData(): Flow<NetworkState<Unit>> =
	map {
		when (it) {
			is NetworkState.Fetching -> it as NetworkState.Fetching<Unit>
			is NetworkState.Fail -> it as NetworkState.Fail<Unit>
			is NetworkState.Success -> NetworkState.success(Unit)
		}
	}