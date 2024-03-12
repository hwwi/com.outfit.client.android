package com.outfit.client.android.ui.bookmarks

import androidx.lifecycle.*
import androidx.paging.Config
import androidx.paging.PagedList
import androidx.paging.PagingRequestHelper
import androidx.paging.toLiveData
import com.outfit.client.android.data.NetworkState
import com.outfit.client.android.data.args.ConnectionArgs
import com.outfit.client.android.data.args.Direction
import com.outfit.client.android.data.args.SortOrder
import com.outfit.client.android.data.model.Shot
import com.outfit.client.android.extension.last
import com.outfit.client.android.livedata.Event
import com.outfit.client.android.livedata.toEvent
import com.outfit.client.android.repository.CoroutineBoundaryCallback
import com.outfit.client.android.repository.ShotRepository
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

class BookmarksViewModel @Inject constructor(
	private val shotRepository: ShotRepository
) : ViewModel() {

	private val networkPageSize: Int = 30

	private val shotsBoundaryCallback = object : CoroutineBoundaryCallback<Shot>(viewModelScope) {
		override suspend fun onRequest(connectionArgs: ConnectionArgs): NetworkState<Any> =
			shotRepository.fetchViewerBookmarkConnection(connectionArgs, false).last()

		override suspend fun buildArgs(
			requestType: PagingRequestHelper.RequestType,
			item: Shot?
		): ConnectionArgs = ConnectionArgs.reverse(requestType, item, networkPageSize)
	}

	val shotList: LiveData<PagedList<Shot>> =
		shotRepository
			.findAllBookmarks()
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
				.fetchViewerBookmarkConnection(
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
