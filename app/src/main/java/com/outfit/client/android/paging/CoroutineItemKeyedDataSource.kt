package com.outfit.client.android.paging

import androidx.paging.ItemKeyedDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class CoroutineItemKeyedDataSource<Key, T>(
	private val coroutineScope: CoroutineScope
) : ItemKeyedDataSource<Key, T>() {
	enum class DataSourceDirection {
		Initial,
		After,
		Before
	}

	abstract suspend fun onRequestSource(
		dataSourceDirection: DataSourceDirection,
		key: Key?,
		requestedLoadSize: Int
	): List<T>

	final override fun loadInitial(
		params: LoadInitialParams<Key>,
		callback: LoadInitialCallback<T>
	) {
		coroutineScope.launch {
			try {
				val result = onRequestSource(
					DataSourceDirection.Initial,
					params.requestedInitialKey,
					params.requestedLoadSize
				)
				callback.onResult(result)
			} catch (e: Exception) {
				Timber.d(e)
			}
		}
	}

	final override fun loadAfter(params: LoadParams<Key>, callback: LoadCallback<T>) {
		coroutineScope.launch {
			try {
				val result = onRequestSource(
					DataSourceDirection.After,
					params.key,
					params.requestedLoadSize
				)
				callback.onResult(result)
			} catch (e: Exception) {
				Timber.d(e)
			}
		}
	}

	final override fun loadBefore(params: LoadParams<Key>, callback: LoadCallback<T>) {
		coroutineScope.launch {
			try {
				val result = onRequestSource(
					DataSourceDirection.Before,
					params.key,
					params.requestedLoadSize
				)
				callback.onResult(result)
			} catch (e: Exception) {
				Timber.d(e)
			}
		}
	}
}