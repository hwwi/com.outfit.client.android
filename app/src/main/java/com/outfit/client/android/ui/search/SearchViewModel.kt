package com.outfit.client.android.ui.search

import androidx.lifecycle.*
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import com.outfit.client.android.data.NetworkState
import com.outfit.client.android.data.payload.SearchGetPayload
import com.outfit.client.android.extension.filterData
import com.outfit.client.android.extension.ignoreData
import com.outfit.client.android.livedata.Event
import com.outfit.client.android.livedata.toEvent
import com.outfit.client.android.service.SearchService
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class SearchViewModel @Inject constructor(
	private val firebaseAnalytics: FirebaseAnalytics,
	private val searchService: SearchService
) : ViewModel() {

	private val _searchInput = MutableLiveData<String>()
	private val _searchPayload: LiveData<NetworkState<SearchGetPayload>> = _searchInput
		.asFlow()
		.debounce(500)
		.flatMapLatest {
			when {
				it.isBlank() -> flowOf(
					NetworkState.success(SearchGetPayload(null, null, null))
				)
				else -> {
					firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SEARCH) {
						param(FirebaseAnalytics.Param.SEARCH_TERM, it)
					}
					searchService.loadSearch(it)
				}
			}
		}
		.asLiveData()
	val searchPayload: LiveData<SearchGetPayload> = _searchPayload
		.filterData()
	val searchResultState: LiveData<Event<NetworkState<Unit>>> = _searchPayload
		.ignoreData()
		.toEvent()

	fun search(text: String) {
		_searchInput.postValue(text)
	}

}