package com.outfit.client.android.ui.post.tagging

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import com.outfit.client.android.data.args.ItemTagArgs
import com.outfit.client.android.di.AssistedPreviousBackStackEntryViewModelFactory
import com.outfit.client.android.network.api.ShotApi
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import java.io.File
import kotlin.collections.set

class TaggingViewModel @AssistedInject constructor(
	@Assisted private val previousBackStackEntry: NavBackStackEntry,
	private val shotApi: ShotApi
) : ViewModel() {

	@AssistedInject.Factory
	interface Factory : AssistedPreviousBackStackEntryViewModelFactory

	companion object {
		const val KEY_FILE_PER_ITEM_TAGS =
			"com.outfit.client.android.ui.post.tagging.TaggingViewModel.URI_TO_ITEM_TAGS"
	}

	private val _filePerItemTagList =
		MutableLiveData<MutableMap<File, MutableList<ItemTagArgs>>>(
			previousBackStackEntry.savedStateHandle[KEY_FILE_PER_ITEM_TAGS] ?: mutableMapOf()
		)
	val filePerItemTags: LiveData<out Map<File, List<ItemTagArgs>>> =
		_filePerItemTagList

	fun addTag(file: File, itemTag: ItemTagArgs) {
		val value = _filePerItemTagList.value
			?.mapValuesTo(mutableMapOf()) { it.value.toMutableList() }
		value ?: return
		var tagList = value[file]
		if (tagList == null) {
			tagList = mutableListOf()
			value[file] = tagList
		}
		tagList.add(itemTag)
		_filePerItemTagList.value = value
	}

	fun removeTagList(file: File, itemTag: ItemTagArgs) {
		val value = _filePerItemTagList.value
			?.mapValuesTo(mutableMapOf()) { it.value.toMutableList() }
		value ?: return
		if (value[file]?.remove(itemTag) == true) {
			_filePerItemTagList.value = value
		}
	}
}