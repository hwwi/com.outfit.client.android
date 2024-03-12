package com.outfit.client.android.ui.post.modifyitemtag

import android.content.Context
import androidx.lifecycle.*
import com.outfit.client.android.R
import com.outfit.client.android.data.NetworkState
import com.outfit.client.android.data.payload.SearchGetPayload
import com.outfit.client.android.di.AssistedArgsViewModelFactory
import com.outfit.client.android.extension.filterData
import com.outfit.client.android.network.api.SearchApi
import com.outfit.client.android.network.api.TagApi
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ModifyItemTagViewModel @AssistedInject constructor(
	@Assisted private val args: ModifyItemTagDialogArgs,
	private val context: Context,
	private val tagApi: TagApi,
	private val searchApi: SearchApi
) : ViewModel() {

	@AssistedInject.Factory
	interface Factory : AssistedArgsViewModelFactory<ModifyItemTagDialogArgs>

	companion object {
		const val KEY_FILE_TO_ITEM_TAG_ARGS =
			"com.outfit.client.android.ui.post.modifyitemtag.ModifyItemTagViewModel.KEY_FILE_TO_ITEM_TAG_ARGS"
	}

	private val validateInput = MutableLiveData<Pair<String?, String?>>()
	val validateState: LiveData<NetworkState<Unit>> = validateInput
		.asFlow()
		.debounce(500)
		.flatMapLatest {
			val brandCode = it.first?.trim() ?: ""
			val productCode = it.second?.trim() ?: ""
			tagApi.getValidateItem(brandCode, productCode)
		}
		.asLiveData()

	fun validate(brandCode: String?, productCode: String?) {
		validateInput.postValue(brandCode to productCode)
	}

	private val searchInput = MutableLiveData<Pair<String?, String?>>()

	val searchResult: LiveData<SearchGetPayload> = searchInput
		.asFlow()
		.debounce(500)
		.flatMapLatest {
			val brandCode = it.first?.trim() ?: ""
			val productCode = it.second?.trim() ?: ""

			when {
				brandCode.isEmpty() -> flowOf(SearchGetPayload(null, null, null))
				else -> searchApi
					.getSearch(
						when {
							productCode.isEmpty() -> context.getString(
								R.string.tag_brand,
								brandCode
							)
							else -> context.getString(R.string.tag_product, brandCode, productCode)
						}
					)
					.filterData()
			}
		}
		.asLiveData()

	fun search(brandCode: String?, productCode: String?) {
		searchInput.postValue(brandCode to productCode)
	}

	fun clearSearch() {
		searchInput.postValue(null to null)
	}

}