package com.outfit.client.android.ui.authentication.signup

import androidx.lifecycle.*
import androidx.navigation.NavBackStackEntry
import com.outfit.client.android.data.NetworkState
import com.outfit.client.android.data.VerificationMethod
import com.outfit.client.android.data.args.PersonPostArgs
import com.outfit.client.android.data.payload.PostRequestAnonymousVerificationPayload
import com.outfit.client.android.di.AssistedCurrentBackStackEntryViewModelFactory
import com.outfit.client.android.extension.onSuccess
import com.outfit.client.android.pref.SessionPref
import com.outfit.client.android.repository.PersonRepository
import com.outfit.client.android.ui.anonymousverification.verify.VerifyCodeViewModel
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest

class SignUpViewModel @AssistedInject constructor(
	@Assisted private val currentBackStackEntry: NavBackStackEntry,
	private var personRepository: PersonRepository
) : ViewModel() {

	@AssistedInject.Factory
	interface Factory : AssistedCurrentBackStackEntryViewModelFactory

	val isVerificated: Boolean
		get() = currentBackStackEntry.savedStateHandle.contains(VerifyCodeViewModel.KEY_VERIFICATION_PAYLOAD)
	val verificationPayload: LiveData<PostRequestAnonymousVerificationPayload>
		get() = currentBackStackEntry.savedStateHandle.getLiveData(VerifyCodeViewModel.KEY_VERIFICATION_PAYLOAD)

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

	private val postPersonRequest = MutableLiveData<PersonPostArgs>()

	val networkState: LiveData<NetworkState<Unit>> = postPersonRequest
		.switchMap { req ->
			personRepository.postPerson(req)
				.onSuccess {
					SessionPref.lastSignInPhoneOrEmailOrName = req.email
				}
				.asLiveData()
		}

	fun fetchPostPerson(
		name: String,
		password: String,
		confirmPassword: String
	) {
		val payload = verificationPayload.value
			?: throw IllegalStateException("verificationPayload.value is null")

		val email: String?
		val phoneNumber: String?

		when (payload.method) {
			VerificationMethod.Sms -> {
				phoneNumber = payload.to
				email = null
			}
			VerificationMethod.Email -> {
				email = payload.to
				phoneNumber = null
			}
		}
		val args = PersonPostArgs(
			email,
			phoneNumber,
			payload.verificationId,
			name,
			password,
			confirmPassword
		)
		postPersonRequest.postValue(args)
	}
}