package com.outfit.client.android.ui.viewitemtag

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Config
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import com.outfit.client.android.data.model.Shot
import com.outfit.client.android.di.AssistedSavedStateArgsViewModelFactory
import com.outfit.client.android.repository.ShotRepository
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject

class ViewItemTagViewModel @AssistedInject constructor(
	@Assisted private val savedState: SavedStateHandle,
	@Assisted private val args: ViewItemTagFragmentArgs,
	private val firebaseAnalytics: FirebaseAnalytics,
	private val shotRepository: ShotRepository
) : ViewModel() {
	init {
		firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT) {
			param(FirebaseAnalytics.Param.CONTENT_TYPE, "ViewItemTag")
			param(FirebaseAnalytics.Param.ITEM_ID, "${args.brandCode}|${args.productCode}")
		}
	}

	@AssistedInject.Factory
	interface Factory : AssistedSavedStateArgsViewModelFactory<ViewItemTagFragmentArgs>

	private val networkPageSize: Int = 10

	private val _dataSourceFactory = shotRepository
		.getConnectionByBrandProductTag(viewModelScope, args.brandCode, args.productCode)

	val shots: LiveData<PagedList<Shot>> =
		_dataSourceFactory
			.toLiveData(
				config = Config(pageSize = networkPageSize, enablePlaceholders = false)
			)

	val shotsRefreshState: LiveData<Boolean> = _dataSourceFactory.invalidateState

	fun refresh() {
		_dataSourceFactory.invalidate()
	}
}
