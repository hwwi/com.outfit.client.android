package com.outfit.client.android.ui.subscriptions

import android.content.Context
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

class SubscriptionsViewModel @AssistedInject constructor(
	@Assisted private val savedState: SavedStateHandle,
	private val context: Context,
	private val shotRepository: ShotRepository
) : ViewModel() {

	@AssistedInject.Factory
	interface Factory : AssistedSavedStateViewModelFactory

	private val networkPageSize: Int = 10

	private val shotsBoundaryCallback = object : CoroutineBoundaryCallback<Shot>(viewModelScope) {
		override suspend fun onRequest(connectionArgs: ConnectionArgs): NetworkState<Any> {
			return shotRepository.fetchConnectionByFollowing(CacheType.SUBSCRIPTIONS,
				connectionArgs
			).last()
		}

		override suspend fun buildArgs(
			requestType: PagingRequestHelper.RequestType,
			item: Shot?
		): ConnectionArgs = ConnectionArgs.reverse(requestType, item, networkPageSize)
	}
	val shots: LiveData<PagedList<Shot>> =
		shotRepository
			.find(CacheType.SUBSCRIPTIONS)
			.toLiveData(
				config = Config(
					pageSize = networkPageSize,
					initialLoadSizeHint = networkPageSize * 2,
					enablePlaceholders = false
				),
				boundaryCallback = shotsBoundaryCallback
			)
	val shotsNetworkState: LiveData<Event<NetworkState<Unit>>> =
		shotsBoundaryCallback.networkState.toEvent()

	fun retry() {
		shotsBoundaryCallback.retryAllFailed()
	}

	private val refresh = MutableLiveData<Unit>()
	val shotsRefreshState: LiveData<Event<NetworkState<Unit>>> = refresh
		.switchMap {
			shotRepository
				.fetchConnectionByFollowing(
					CacheType.SUBSCRIPTIONS,
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
