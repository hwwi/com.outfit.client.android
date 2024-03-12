package com.outfit.client.android.ui.explore

import androidx.lifecycle.*
import androidx.paging.Config
import androidx.paging.PagedList
import androidx.paging.PagingRequestHelper
import androidx.paging.toLiveData
import com.outfit.client.android.data.CacheType
import com.outfit.client.android.data.NetworkState
import com.outfit.client.android.data.args.ConnectionArgs
import com.outfit.client.android.data.args.Direction
import com.outfit.client.android.data.args.SortOrder
import com.outfit.client.android.data.model.Shot
import com.outfit.client.android.di.AssistedSavedStateViewModelFactory
import com.outfit.client.android.extension.last
import com.outfit.client.android.livedata.Event
import com.outfit.client.android.livedata.toEvent
import com.outfit.client.android.repository.CoroutineBoundaryCallback
import com.outfit.client.android.repository.ShotRepository
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.flow.collect

class ExploreViewModel @AssistedInject constructor(
	@Assisted private val savedState: SavedStateHandle,
	private val shotRepository: ShotRepository
) : ViewModel() {

	@AssistedInject.Factory
	interface Factory : AssistedSavedStateViewModelFactory

	private val networkPageSize: Int = 20

	private val shotsBoundaryCallback = object : CoroutineBoundaryCallback<Shot>(viewModelScope) {
		override suspend fun onRequest(connectionArgs: ConnectionArgs): NetworkState<Any> =
			shotRepository.fetchConnection(CacheType.EXPLORE, connectionArgs).last()

		override suspend fun buildArgs(
			requestType: PagingRequestHelper.RequestType,
			item: Shot?
		): ConnectionArgs = ConnectionArgs.reverse(requestType, item, networkPageSize)
	}

	val shotList: LiveData<PagedList<Shot>> =
		shotRepository
			.find(CacheType.EXPLORE)
			.toLiveData(
				config = Config(pageSize = networkPageSize, enablePlaceholders = false),
				boundaryCallback = shotsBoundaryCallback
			)
	val shotListState: LiveData<Event<NetworkState<Unit>>> =
		shotsBoundaryCallback.networkState.toEvent()
	fun retry() {
		shotsBoundaryCallback.retryAllFailed()
	}

	private val refresh = MutableLiveData<Unit>()
	val shotsRefreshState: LiveData<Event<NetworkState<Unit>>> = refresh
		.switchMap {
			shotRepository
				.fetchConnection(
					CacheType.EXPLORE,
					ConnectionArgs(
						null,
						Direction.PREVIOUS,
						SortOrder.DESCENDING,
						networkPageSize
					),
					true
				)
				.asLiveData()
		}
		.toEvent()

	fun refresh() {
		refresh.postValue(Unit)
	}
}
