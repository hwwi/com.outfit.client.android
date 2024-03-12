package com.outfit.client.android.paging

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource

abstract class InvalidateableDataSourceFactory<Key, Value> : DataSource.Factory<Key, Value>() {
	private var latestDataSource: DataSource<Key, Value>? = null
	private val _invalidateState: MutableLiveData<Boolean> = MutableLiveData()
	val invalidateState: LiveData<Boolean> = _invalidateState

	abstract fun onCreate(): DataSource<Key, Value>

	final override fun create(): DataSource<Key, Value> {
		_invalidateState.postValue(false)
		return onCreate().apply {
			latestDataSource = this
			addInvalidatedCallback {
				_invalidateState.postValue(false)
			}
		}
	}

	fun invalidate() {
		latestDataSource?.apply {
			_invalidateState.postValue(true)
			invalidate()
		}
	}
}