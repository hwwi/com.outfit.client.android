package com.outfit.client.android.ui.authentication.resetpassword

import androidx.lifecycle.*
import androidx.navigation.NavBackStackEntry
import com.outfit.client.android.data.NetworkState
import com.outfit.client.android.data.args.AccountResetPasswordPatchArgs
import com.outfit.client.android.data.payload.PostRequestAnonymousVerificationPayload
import com.outfit.client.android.di.AssistedCurrentBackStackEntryViewModelFactory
import com.outfit.client.android.repository.AccountRepository
import com.outfit.client.android.ui.anonymousverification.verify.VerifyCodeViewModel
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject

class ResetPasswordViewModel @AssistedInject constructor(
	@Assisted private val currentBackStackEntry: NavBackStackEntry,
	private val accountRepository: AccountRepository
) : ViewModel() {

	@AssistedInject.Factory
	interface Factory : AssistedCurrentBackStackEntryViewModelFactory

	val isVerificated: Boolean
		get() = currentBackStackEntry.savedStateHandle.contains(VerifyCodeViewModel.KEY_VERIFICATION_PAYLOAD)

	private val _accountPasswordPatchArgs = MutableLiveData<AccountResetPasswordPatchArgs>()

	val networkState: LiveData<NetworkState<Unit>> = _accountPasswordPatchArgs
		.switchMap {
			accountRepository
				.resetPassword(it)
				.asLiveData()
		}

	fun resetPassword(
		newPassword: String,
		confirmPassword: String
	) {
		val payload = currentBackStackEntry.savedStateHandle
			.get<PostRequestAnonymousVerificationPayload>(VerifyCodeViewModel.KEY_VERIFICATION_PAYLOAD)
			?: throw IllegalStateException("verificationPayload.value is null")

		_accountPasswordPatchArgs.postValue(
			AccountResetPasswordPatchArgs(
				payload.verificationId,
				newPassword,
				confirmPassword
			)
		)
	}
}
