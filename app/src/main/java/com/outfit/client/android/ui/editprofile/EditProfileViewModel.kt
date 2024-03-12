package com.outfit.client.android.ui.editprofile

import androidx.lifecycle.*
import androidx.navigation.NavBackStackEntry
import com.outfit.client.android.data.NetworkState
import com.outfit.client.android.data.model.PersonDetail
import com.outfit.client.android.di.AssistedCurrentBackStackEntryViewModelFactory
import com.outfit.client.android.extension.filterData
import com.outfit.client.android.extension.ignoreData
import com.outfit.client.android.pref.SessionPref
import com.outfit.client.android.repository.AccountRepository
import com.outfit.client.android.repository.PersonRepository
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject

class EditProfileViewModel @AssistedInject constructor(
	@Assisted private val currentBackStackEntry: NavBackStackEntry,
	private val personRepository: PersonRepository,
	private val accountRepository: AccountRepository
) : ViewModel() {

	@AssistedInject.Factory
	interface Factory : AssistedCurrentBackStackEntryViewModelFactory

	private val _personDetailResult: LiveData<NetworkState<PersonDetail>> = personRepository
		.findDetailOneById(SessionPref.id)

	val personDetail: LiveData<PersonDetail> = _personDetailResult
		.filterData()
	val personDetailState: LiveData<NetworkState<Unit>> = _personDetailResult
		.ignoreData()

	private val _accountPutArgs = MutableLiveData<String?>()

	val submitState: LiveData<NetworkState<Unit>> = _accountPutArgs
		.switchMap {
			accountRepository
				.update(it)
				.asLiveData()
		}

	fun submitChanges(biography: String?) {
		_accountPutArgs.postValue(biography)
	}
}
