package com.outfit.client.android.ui.anonymousverification.request

import androidx.lifecycle.*
import com.outfit.client.android.data.NetworkState
import com.outfit.client.android.data.payload.PostRequestAnonymousVerificationPayload
import com.outfit.client.android.di.AssistedArgsViewModelFactory
import com.outfit.client.android.livedata.toLiveEvent
import com.outfit.client.android.network.api.VerificationApi
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers

class RequestCodeViewModel @AssistedInject constructor(
	@Assisted private val args: RequestCodeFragmentArgs,
	private val verificationApi: VerificationApi
) : ViewModel() {

	@AssistedInject.Factory
	interface Factory : AssistedArgsViewModelFactory<RequestCodeFragmentArgs>

	private val _requestVerificationArgs = MutableLiveData<Pair<String, String?>>()
	private val _requestVerificationState: LiveData<NetworkState<PostRequestAnonymousVerificationPayload>> =
		_requestVerificationArgs
			.switchMap {
				val to = it.first
				when (val region = it.second) {
					null -> verificationApi.postRequestAnonymousEmailVerification(args.purpose, to)
					else -> verificationApi.postRequestAnonymousSmsVerification(
						args.purpose,
						to,
						region
					)
				}
					.asLiveData(viewModelScope.coroutineContext + Dispatchers.IO)
			}

	val networkState: LiveData<NetworkState<PostRequestAnonymousVerificationPayload>> =
		_requestVerificationState.toLiveEvent()

	fun requestVerification(email: String) =
		_requestVerificationArgs.postValue(email to null)

	fun requestVerification(numberToParse: String, region: String) =
		_requestVerificationArgs.postValue(numberToParse to region)
}