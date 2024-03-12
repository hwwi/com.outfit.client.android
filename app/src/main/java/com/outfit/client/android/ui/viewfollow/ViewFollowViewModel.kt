package com.outfit.client.android.ui.viewfollow

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import com.outfit.client.android.data.NetworkState
import com.outfit.client.android.data.model.PersonDetail
import com.outfit.client.android.di.AssistedArgsViewModelFactory
import com.outfit.client.android.extension.filterData
import com.outfit.client.android.extension.ignoreData
import com.outfit.client.android.repository.PersonRepository
import com.outfit.client.android.repository.ShotRepository
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject

class ViewFollowViewModel @AssistedInject constructor(
	@Assisted private val args: ViewFollowFragmentArgs,
	private val firebaseAnalytics: FirebaseAnalytics,
	private val shotRepository: ShotRepository,
	private val personRepository: PersonRepository
) : ViewModel() {

	init {
		firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT) {
			param(FirebaseAnalytics.Param.CONTENT_TYPE, "ViewFollow")
			param(FirebaseAnalytics.Param.ITEM_ID, args.personId)
		}
	}

	@AssistedInject.Factory
	interface Factory : AssistedArgsViewModelFactory<ViewFollowFragmentArgs>

	private val _personDetailState: LiveData<NetworkState<PersonDetail>> =
		personRepository.findDetailOneById(args.personId)
	val person: LiveData<PersonDetail> = _personDetailState.filterData()
	val personState: LiveData<NetworkState<Unit>> = _personDetailState.ignoreData()

}
