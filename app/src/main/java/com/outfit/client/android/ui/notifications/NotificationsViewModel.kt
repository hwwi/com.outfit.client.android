package com.outfit.client.android.ui.notifications

import android.content.Context
import androidx.lifecycle.*
import androidx.paging.Config
import androidx.paging.PagedList
import androidx.paging.PagingRequestHelper
import androidx.paging.toLiveData
import com.outfit.client.android.data.NetworkState
import com.outfit.client.android.data.args.ConnectionArgs
import com.outfit.client.android.data.args.Direction
import com.outfit.client.android.data.args.SortOrder
import com.outfit.client.android.data.model.Notification
import com.outfit.client.android.extension.last
import com.outfit.client.android.repository.CoroutineBoundaryCallback
import com.outfit.client.android.repository.NotificationRepository
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

class NotificationsViewModel @Inject constructor(
	private val context: Context,
	private val notificationRepository: NotificationRepository
) : ViewModel() {
	private val networkPageSize: Int = 20

	private val boundaryCallback =
		object : CoroutineBoundaryCallback<Notification>(viewModelScope) {
			override suspend fun onRequest(connectionArgs: ConnectionArgs): NetworkState<Any> =
				notificationRepository.fetchConnection(connectionArgs).last()

			override suspend fun buildArgs(
				requestType: PagingRequestHelper.RequestType,
				item: Notification?
			): ConnectionArgs = ConnectionArgs.reverse(requestType, item, networkPageSize)
		}

	val notifications: LiveData<PagedList<Notification>> =
		notificationRepository
			.findAll()
			.toLiveData(
				config = Config(pageSize = networkPageSize, enablePlaceholders = false),
				boundaryCallback = boundaryCallback
			)

	val fetchState: LiveData<NetworkState<Unit>> =
		boundaryCallback.networkState

	fun fetchRetry() {
		boundaryCallback.retryAllFailed()
	}

	private val refresh = MutableLiveData<Unit>()
	val refreshState: LiveData<NetworkState<Unit>> = refresh
		.switchMap {
			notificationRepository
				.fetchConnection(
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

	fun refresh() {
		refresh.postValue(Unit)
	}
}
