package com.outfit.client.android.ui.viewcomments

import android.content.Context
import androidx.lifecycle.*
import androidx.paging.Config
import androidx.paging.PagedList
import androidx.paging.PagingRequestHelper
import androidx.paging.toLiveData
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import com.outfit.client.android.data.NetworkState
import com.outfit.client.android.data.args.ConnectionArgs
import com.outfit.client.android.data.args.Direction
import com.outfit.client.android.data.args.SortOrder
import com.outfit.client.android.data.model.Comment
import com.outfit.client.android.data.model.Shot
import com.outfit.client.android.di.AssistedArgsViewModelFactory
import com.outfit.client.android.extension.filterData
import com.outfit.client.android.extension.ignoreData
import com.outfit.client.android.extension.last
import com.outfit.client.android.repository.CommentRepository
import com.outfit.client.android.repository.CoroutineBoundaryCallback
import com.outfit.client.android.repository.ShotRepository
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject

class ViewCommentsViewModel @AssistedInject constructor(
	@Assisted private val args: ViewCommentsFragmentArgs,
	private val firebaseAnalytics: FirebaseAnalytics,
	private val context: Context,
	private val shotRepository: ShotRepository,
	private val commentRepository: CommentRepository
) : ViewModel() {

	init {
		firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT) {
			param(FirebaseAnalytics.Param.CONTENT_TYPE, "ViewComments")
			param(
				FirebaseAnalytics.Param.ITEM_ID,
				"${args.shotId}|${args.nullableScrollToCommentId}"
			)
		}
	}

	@AssistedInject.Factory
	interface Factory : AssistedArgsViewModelFactory<ViewCommentsFragmentArgs>

	private val networkPageSize: Int = 10

	private val _shotResult: LiveData<NetworkState<Shot>> = when {
		args.showShotPreview -> shotRepository.findCachedOneById(args.shotId)
		else -> MutableLiveData()
	}
	val shot: LiveData<Shot> = _shotResult.filterData()
	val shotState: LiveData<NetworkState<Unit>> = _shotResult.ignoreData()

	private val commentsBoundaryCallback =
		object : CoroutineBoundaryCallback<Comment>(viewModelScope) {
			override suspend fun onRequest(connectionArgs: ConnectionArgs): NetworkState<Any> =
				commentRepository.fetchCommentConnectionByShotId(args.shotId, connectionArgs).last()

			override suspend fun buildArgs(
				requestType: PagingRequestHelper.RequestType,
				item: Comment?
			): ConnectionArgs = ConnectionArgs.forward(requestType, item, networkPageSize)
		}
	val comments: LiveData<PagedList<Comment>> = commentRepository
		.findCachedDataSourceFactoryByShotId(args.shotId)
		.toLiveData(
			config = Config(pageSize = networkPageSize, enablePlaceholders = false),
			//TODO
//			initialLoadKey = args.nullableScrollToCommentId,
			boundaryCallback = commentsBoundaryCallback
		)
	val commentsNetworkState: LiveData<NetworkState<Unit>> = commentsBoundaryCallback.networkState

	fun retryComments() {
		commentsBoundaryCallback.retryAllFailed()
	}

	private val refresh = MutableLiveData<Unit>()
	val commentsRefreshState: LiveData<NetworkState<Unit>> = refresh
		.switchMap {
			commentRepository
				.refreshCommentConnectionByShotId(
					args.shotId,
					ConnectionArgs(
						null,
						Direction.AFTER,
						SortOrder.ASCENDING,
						networkPageSize
					)
				)
				.asLiveData()
		}

	fun refreshComments() {
		refresh.postValue(Unit)
	}
}
