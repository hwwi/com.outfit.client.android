package com.outfit.client.android.ui.changepassword

import androidx.lifecycle.*
import com.outfit.client.android.data.NetworkState
import com.outfit.client.android.data.args.AccountPasswordPatchArgs
import com.outfit.client.android.repository.AccountRepository
import javax.inject.Inject

class ChangePasswordViewModel @Inject constructor(
	private val accountRepository: AccountRepository
) : ViewModel() {

	private val _accountPasswordPatchArgs = MutableLiveData<AccountPasswordPatchArgs>()

	val submitState: LiveData<NetworkState<Unit>> = _accountPasswordPatchArgs
		.switchMap {
			accountRepository
				.patchPassword(it)
				.asLiveData()
		}

	fun submitChanges(
		currentPassword: String,
		newPassword: String,
		confirmPassword: String
	) {
		_accountPasswordPatchArgs.postValue(
			AccountPasswordPatchArgs(
				currentPassword,
				newPassword,
				confirmPassword
			)
		)
	}
}
