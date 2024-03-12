package com.outfit.client.android.ui.anonymousverification.verify

import androidx.lifecycle.*
import com.outfit.client.android.data.NetworkState
import com.outfit.client.android.data.payload.PostRequestAnonymousVerificationPayload
import com.outfit.client.android.di.AssistedArgsViewModelFactory
import com.outfit.client.android.extension.filterData
import com.outfit.client.android.livedata.LiveEvent
import com.outfit.client.android.livedata.toLiveEvent
import com.outfit.client.android.network.api.VerificationApi
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject

class VerifyCodeViewModel @AssistedInject constructor(
	@Assisted private val args: VerifyCodeFragmentArgs,
	private val verificationApi: VerificationApi
) : ViewModel() {

	companion object {
		const val KEY_VERIFICATION_PAYLOAD =
			"com.outfit.client.android.ui.anonymousverification.AnonymousVerificationFragment.key.verification_payload"
	}


	@AssistedInject.Factory
	interface Factory :
		AssistedArgsViewModelFactory<VerifyCodeFragmentArgs>

	private val _verifyCodeArgs = MutableLiveData<String>()
	private val _verifyCodeState: LiveData<NetworkState<Unit>> =
		_verifyCodeArgs
			.switchMap {
				verificationApi.getVerifyCode(args.requestVerificationPayload.verificationId, it)
					.asLiveData()
			}

	fun verifyCode(code: String) = _verifyCodeArgs.postValue(code)

	private val _requestNewCodeArgs = MutableLiveData<Unit>()
	private val _requestNewCodeState: LiveData<NetworkState<Unit>> =
		_requestNewCodeArgs
			.switchMap {
				verificationApi.getRequestNewCode(args.requestVerificationPayload.verificationId)
					.asLiveData()
			}

	fun requestNewCode() = _requestNewCodeArgs.postValue(Unit)

	val networkState: LiveData<NetworkState<Unit>> = LiveEvent<NetworkState<Unit>>().apply {
		addSource(_verifyCodeState) { value = it }
		addSource(_requestNewCodeState) { value = it }
	}

	val onVerifySuccess: LiveData<PostRequestAnonymousVerificationPayload> = _verifyCodeState
		.filterData()
		.map {
			args.requestVerificationPayload
		}
		.toLiveEvent()
}