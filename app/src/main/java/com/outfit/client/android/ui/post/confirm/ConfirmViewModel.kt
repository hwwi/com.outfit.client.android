package com.outfit.client.android.ui.post.confirm

import android.content.Context
import androidx.lifecycle.*
import androidx.navigation.NavBackStackEntry
import com.outfit.client.android.data.NetworkState
import com.outfit.client.android.data.args.ItemTagArgs
import com.outfit.client.android.data.args.ShotPostArgs
import com.outfit.client.android.data.args.TagListAndFileIndex
import com.outfit.client.android.di.AssistedCurrentBackStackEntryViewModelFactory
import com.outfit.client.android.extension.ignoreData
import com.outfit.client.android.extension.onSuccess
import com.outfit.client.android.network.api.ShotApi
import com.outfit.client.android.ui.post.tagging.TaggingViewModel
import com.outfit.client.android.ui.selectimage.SelectImageNav
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class ConfirmViewModel @AssistedInject constructor(
	@Assisted private val currentBackStackEntry: NavBackStackEntry,
	private val context: Context,
	private val shotApi: ShotApi
) : ViewModel() {

	@AssistedInject.Factory
	interface Factory : AssistedCurrentBackStackEntryViewModelFactory

	val isImageSelected: Boolean
		get() = currentBackStackEntry.savedStateHandle.contains(SelectImageNav.KEY_CACHE_FILES)
	val files: List<File>
		get() = currentBackStackEntry.savedStateHandle[SelectImageNav.KEY_CACHE_FILES]
			?: emptyList()
	val filePerItemTags: LiveData<Map<File, List<ItemTagArgs>>>
		get() = currentBackStackEntry.savedStateHandle.getLiveData(TaggingViewModel.KEY_FILE_PER_ITEM_TAGS)

	fun clearFiles() {
		currentBackStackEntry.savedStateHandle.apply {
			remove<Map<File, List<ItemTagArgs>>>(TaggingViewModel.KEY_FILE_PER_ITEM_TAGS)
			remove<List<File>>(SelectImageNav.KEY_CACHE_FILES)
				?.forEach {
					it.delete()
				}
		}
	}

	private val content = MutableLiveData<String>()
	val networkState: LiveData<NetworkState<Unit>> = content
		.switchMap { content ->

			val files = files
			val filePartList = files.mapIndexed { index, file ->
				MultipartBody.Part.createFormData(
					"files",
					"$index.jpeg",
					file.asRequestBody("image/jpeg".toMediaTypeOrNull())
				)
			}

			val tagListAndFileIndexList = currentBackStackEntry.savedStateHandle
				.get<Map<File, List<ItemTagArgs>>>(TaggingViewModel.KEY_FILE_PER_ITEM_TAGS)
				?.toList()
				?.map { pair ->
					TagListAndFileIndex(files.indexOf(pair.first), pair.second)
				}
				?: emptyList()

			shotApi
				.postOne(
					ShotPostArgs(
						content,
						tagListAndFileIndexList
					),
					filePartList
				)
				.onSuccess {
					clearFiles()
				}
				.ignoreData()
				.asLiveData()
		}

	fun postNewShot(newContent: String) {
		content.postValue(newContent)
	}
}