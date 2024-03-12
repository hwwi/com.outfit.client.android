package com.outfit.client.android.ui.closet

import android.content.Context
import androidx.lifecycle.*
import androidx.paging.Config
import androidx.paging.PagedList
import androidx.paging.PagingRequestHelper
import androidx.paging.toLiveData
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import com.outfit.client.android.R
import com.outfit.client.android.data.CacheType
import com.outfit.client.android.data.NetworkState
import com.outfit.client.android.data.args.ConnectionArgs
import com.outfit.client.android.data.args.Direction
import com.outfit.client.android.data.args.SortOrder
import com.outfit.client.android.data.model.PersonDetail
import com.outfit.client.android.data.model.Shot
import com.outfit.client.android.di.AssistedSavedStateArgsViewModelFactory
import com.outfit.client.android.extension.*
import com.outfit.client.android.livedata.Event
import com.outfit.client.android.livedata.toEvent
import com.outfit.client.android.pref.SessionPref
import com.outfit.client.android.repository.CoroutineBoundaryCallback
import com.outfit.client.android.repository.PersonRepository
import com.outfit.client.android.repository.ShotRepository
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf

class ClosetViewModel @AssistedInject constructor(
	@Assisted private val savedState: SavedStateHandle,
	@Assisted private val args: ClosetFragmentArgs,
	private val context : Context,
	private val firebaseAnalytics: FirebaseAnalytics,
	private val personRepository: PersonRepository,
	private val shotRepository: ShotRepository
) : ViewModel() {

	init {
		if (args.personName != null || args.nullablePersonId != 0L)
			firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT) {
				param(FirebaseAnalytics.Param.CONTENT_TYPE, "Closet")
				param(
					FirebaseAnalytics.Param.ITEM_ID,
					args.personName ?: args.nullablePersonId.toString()
				)
			}
	}

	@AssistedInject.Factory
	interface Factory : AssistedSavedStateArgsViewModelFactory<ClosetFragmentArgs>

	private val networkPageSize: Int = 10

	fun setPrivate(isPrivate: Boolean) {
		_isPrivate.value = isPrivate
	}

	private val _isPrivate = MutableLiveData(false)
	val isPrivate: Boolean?
		get() = _isPrivate.value

	private val _personId = MutableLiveData<Long>()
	val personId: LiveData<Long> = _personId

	private val _personDetailResult: LiveData<NetworkState<PersonDetail>> = when {
		args.nullablePersonId != 0L -> {
			_personId.value = args.nullablePersonId
			personRepository.findDetailOneById(args.nullablePersonId)
		}
		args.personName != null -> personRepository.findDetailOneByName(args.personName)
			.onSuccess {
				_personId.value = it.id
			}
		else -> MutableLiveData(NetworkState.fail(Error("Id and Name is null")))
	}
	val personDetail: LiveData<PersonDetail> = _personDetailResult
		.filterData()

	val personDetailState: LiveData<NetworkState<Unit>> = _personDetailResult
		.ignoreData()

	private val _shotsBoundaryCallback = MutableLiveData<CoroutineBoundaryCallback<Shot>>()

	private val _personIdAndIsPrivate = _personId.switchMap { personId ->
		_isPrivate.map { isPrivate -> personId to isPrivate }
	}
	val shots: LiveData<PagedList<Shot>> =
		_personIdAndIsPrivate.switchMap {
			val personId = it.first
			val isPrivate = it.second
			val shotsBoundaryCallback =
				object : CoroutineBoundaryCallback<Shot>(viewModelScope) {
					override suspend fun onRequest(connectionArgs: ConnectionArgs): NetworkState<Any> =
						createFetchFlow(personId, isPrivate, connectionArgs, false).last()

					override suspend fun buildArgs(
						requestType: PagingRequestHelper.RequestType,
						item: Shot?
					): ConnectionArgs = ConnectionArgs.reverse(requestType, item, networkPageSize)
				}
			_shotsBoundaryCallback.value = shotsBoundaryCallback
			shotRepository
				.findByPersonId(
					CacheType.CLOSET,
					personId,
					isPrivate
				)
				.toLiveData(
					config = Config(pageSize = networkPageSize, enablePlaceholders = false),
					boundaryCallback = shotsBoundaryCallback
				)
		}

	val shotsState: LiveData<Event<NetworkState<Unit>>> = _shotsBoundaryCallback
		.switchMap {
			it.networkState
		}
		.toEvent()


	fun retry() {
		_shotsBoundaryCallback.value?.retryAllFailed()
	}

	private val refresh = MutableLiveData<Unit>()
	val shotsRefreshState: LiveData<Event<NetworkState<Unit>>> = refresh
		.mapNotNull {
			_personIdAndIsPrivate.value
		}
		.switchMap {
			val personId = it.first
			val isPrivate = it.second
			createFetchFlow(
				personId,
				isPrivate,
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

	private fun createFetchFlow(
		personId: Long,
		isPrivate: Boolean,
		args: ConnectionArgs,
		isRefresh: Boolean
	): Flow<NetworkState<Unit>> = when {
		!isPrivate -> shotRepository.fetchConnectionByPersonId(
			CacheType.CLOSET,
			personId,
			args,
			isRefresh
		)
		personId == SessionPref.id -> shotRepository.fetchViewersPrivateConnection(
			CacheType.CLOSET,
			args,
			isRefresh
		)
		else -> flowOf(NetworkState.fail(Throwable(context.getString(R.string.msg_you_can_only_see_your_own_secret_shot))))
	}

	fun refresh() = refresh.postValue(Unit)
}
