package com.outfit.client.android.ui.changename

import androidx.lifecycle.*
import com.outfit.client.android.data.NetworkState
import com.outfit.client.android.data.args.AccountPatchNameArgs
import com.outfit.client.android.repository.AccountRepository
import com.outfit.client.android.repository.PersonRepository
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class ChangeNameViewModel @Inject constructor(
	private val personRepository: PersonRepository,
	private val accountRepository: AccountRepository
) : ViewModel() {


	private val _validateNameInput = MutableLiveData<String>()
	val validateNameState: LiveData<NetworkState<Unit>> = _validateNameInput
		.asFlow()
		.debounce(500)
		.flatMapLatest {
			personRepository.getValidateName(it)
		}
		.asLiveData()

	fun validateName(name: String) {
		_validateNameInput.postValue(name)
	}

	private val _args = MutableLiveData<AccountPatchNameArgs>()

	val submitState: LiveData<NetworkState<Unit>> = _args
		.switchMap {
			accountRepository
				.patchName(it)
				.asLiveData()
		}

	fun submitChanges(
		name: String
	) {
		_args.postValue(
			AccountPatchNameArgs(name)
		)
	}
}
