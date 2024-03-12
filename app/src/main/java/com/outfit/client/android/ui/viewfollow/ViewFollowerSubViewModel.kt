package com.outfit.client.android.ui.viewfollow

import androidx.lifecycle.*
import androidx.paging.Config
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.outfit.client.android.data.dto.PersonDto
import com.outfit.client.android.repository.PersonRepository
import javax.inject.Inject

class ViewFollowerSubViewModel @Inject constructor(
	private val personRepository: PersonRepository
) : ViewModel() {

	private val networkPageSize: Int = 30

	private val requestFetchFollowersByKeyword = MutableLiveData<Pair<Long, String?>>()
	val followers: LiveData<PagedList<PersonDto>> = requestFetchFollowersByKeyword.switchMap {
		val personId = it.first
		val keyword = it.second
		personRepository
			.getFollowerConnection(viewModelScope, personId, keyword)
			.toLiveData(
				config = Config(pageSize = networkPageSize, enablePlaceholders = false)
			)
	}

	fun requestFetchFollowersByKeyword(personId: Long, keyword: String?) {
		requestFetchFollowersByKeyword.value = personId to keyword
	}
}
