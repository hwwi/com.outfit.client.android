package com.outfit.client.android.ui.authentication.signin

import androidx.lifecycle.*
import com.chibatching.kotpref.bulk
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.outfit.client.android.data.NetworkState
import com.outfit.client.android.data.args.AuthPostTokenArgs
import com.outfit.client.android.data.args.NotificationPostCloudMessagingTokenArgs
import com.outfit.client.android.di.AssistedSavedStateViewModelFactory
import com.outfit.client.android.extension.ignoreData
import com.outfit.client.android.extension.onSuccess
import com.outfit.client.android.livedata.Event
import com.outfit.client.android.livedata.toEvent
import com.outfit.client.android.network.api.AuthApi
import com.outfit.client.android.pref.CloudMessagingPref
import com.outfit.client.android.pref.IntroducePref
import com.outfit.client.android.pref.SessionPref
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import java.util.*

class SignInViewModel @AssistedInject constructor(
	@Assisted private val savedState: SavedStateHandle,
	private val authApi: AuthApi
) : ViewModel() {
	@AssistedInject.Factory
	interface Factory : AssistedSavedStateViewModelFactory

	private val postTokenArgs = MutableLiveData<AuthPostTokenArgs>()
	val networkState: LiveData<Event<NetworkState<Unit>>> = postTokenArgs
		.switchMap { req ->
			authApi.postToken(req)
				.onSuccess { data ->
					FirebaseCrashlytics.getInstance().setUserId("${data.id}")
					val expiredFirebaseToken = req.cloudMessagingTokens?.expiredTokens
					if (!expiredFirebaseToken.isNullOrEmpty())
						CloudMessagingPref.expiredTokens.removeAll(
							expiredFirebaseToken
						)

					if (IntroducePref.lastId != data.id) {
						IntroducePref.clear()
						IntroducePref.lastId = data.id
					}
					SessionPref.bulk {
						id = data.id
						phoneOrEmailOrName = data.phoneOrEmailOrName
						accessToken = data.accessToken
						refreshToken = data.refreshToken
						lastSignInPhoneOrEmailOrName = data.phoneOrEmailOrName
					}
				}
				.ignoreData()
				.toEvent()
				.asLiveData(viewModelScope.coroutineContext + Dispatchers.IO)
		}

	fun fetchPostPersonToken(
		phoneOrEmailOrName: String,
		password: String
	) {
		postTokenArgs.value = AuthPostTokenArgs(
			phoneOrEmailOrName = phoneOrEmailOrName,
			password = password,
			region = Locale.getDefault().country,
			cloudMessagingTokens = NotificationPostCloudMessagingTokenArgs(
				currentToken = CloudMessagingPref.token,
				expiredTokens = CloudMessagingPref.expiredTokens.toList()
			)
		)
	}
}