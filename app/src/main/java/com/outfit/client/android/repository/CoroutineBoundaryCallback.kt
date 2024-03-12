package com.outfit.client.android.repository

import androidx.annotation.MainThread
import androidx.paging.PagedList
import androidx.paging.PagingRequestHelper
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.outfit.client.android.data.NetworkState
import com.outfit.client.android.data.args.ConnectionArgs
import com.outfit.client.android.util.createStatusLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


//TODO PageInfo를 사용하여 item load 제어
abstract class CoroutineBoundaryCallback<T>(
	private val coroutineScope: CoroutineScope
) : PagedList.BoundaryCallback<T>() {
	private val requestHelper = PagingRequestHelper()
	val networkState = requestHelper.createStatusLiveData()

	abstract suspend fun onRequest(connectionArgs: ConnectionArgs): NetworkState<Any>
	abstract suspend fun buildArgs(
		requestType: PagingRequestHelper.RequestType,
		item: T?
	): ConnectionArgs

	private fun onLoaded(requestType: PagingRequestHelper.RequestType, item: T?) {
		requestHelper.runIfNotRunning(requestType) { cb ->
			coroutineScope.launch {
				try {
					val state = onRequest(buildArgs(requestType, item))
					if (state is NetworkState.Fail) {
						cb.recordFailure(state.error)
						return@launch
					}
					cb.recordSuccess()
				} catch (e: Throwable) {
					Firebase.crashlytics.recordException(e)
					cb.recordFailure(e)
				}
			}
		}
	}

	@MainThread
	override fun onZeroItemsLoaded() =
		onLoaded(PagingRequestHelper.RequestType.INITIAL, null)

	@MainThread
	override fun onItemAtEndLoaded(itemAtEnd: T) =
		onLoaded(PagingRequestHelper.RequestType.AFTER, itemAtEnd)

	@MainThread
	override fun onItemAtFrontLoaded(itemAtFront: T) =
		onLoaded(PagingRequestHelper.RequestType.BEFORE, itemAtFront)

	fun retryAllFailed(): Boolean = requestHelper.retryAllFailed()

}