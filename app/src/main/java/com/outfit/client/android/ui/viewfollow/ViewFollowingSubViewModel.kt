package com.outfit.client.android.ui.viewfollow

import androidx.lifecycle.*
import androidx.paging.Config
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.outfit.client.android.data.dto.PersonDto
import com.outfit.client.android.repository.PersonRepository
import javax.inject.Inject

class ViewFollowingSubViewModel @Inject constructor(
	private val personRepository: PersonRepository
) : ViewModel() {

	private val networkPageSize: Int = 30

	private val requestFetchFollowingsByKeyword = MutableLiveData<Pair<Long, String?>>()
	val followings: LiveData<PagedList<PersonDto>> = requestFetchFollowingsByKeyword.switchMap {
		val personId = it.first
		val keyword = it.second
		personRepository
			.getFollowingConnection(viewModelScope, personId, keyword)
			.toLiveData(
				config = Config(pageSize = networkPageSize, enablePlaceholders = false)
			)
	}

	fun requestFetchFollowingsByKeyword(personId: Long, keyword: String?) {
		requestFetchFollowingsByKeyword.value = personId to keyword
	}

	suspend fun deleteFollowing(personId: Long, onError: (Throwable) -> Unit) {
		try {
			personRepository.deleteFollowing(personId)
			requestFetchFollowingsByKeyword.value = requestFetchFollowingsByKeyword.value
		} catch (e: Exception) {
			onError(e)
		}
	}
}
