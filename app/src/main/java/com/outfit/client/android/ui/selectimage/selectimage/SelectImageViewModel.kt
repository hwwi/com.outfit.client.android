package com.outfit.client.android.ui.selectimage.selectimage

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.MediaStore.Images.ImageColumns
import androidx.core.database.getIntOrNull
import androidx.core.os.bundleOf
import androidx.lifecycle.*
import androidx.paging.*
import com.outfit.client.android.data.vo.BucketEntry
import com.outfit.client.android.di.AssistedSavedStateArgsViewModelFactory
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers

class SelectImageViewModel @AssistedInject constructor(
	@Assisted private val savedState: SavedStateHandle,
	@Assisted private val args: SelectImageFragmentArgs,
	private val context: Context
) : ViewModel() {

	@AssistedInject.Factory
	interface Factory : AssistedSavedStateArgsViewModelFactory<SelectImageFragmentArgs>

	@SuppressLint("InlinedApi")
	val outputExternalImagesBucketPaths: LiveData<Pair<List<BucketEntry?>, Int?>> =
		liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
			val lastSelectedBucketId = savedState.get<Int>(BucketEntry::id.name)

			// https://android.googlesource.com/platform/packages/apps/Gallery2/+/master/src/com/android/gallery3d/data/BucketHelper.java#64
			val bucketEntries = context.contentResolver.query(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				arrayOf(ImageColumns.BUCKET_ID, ImageColumns.BUCKET_DISPLAY_NAME),
				null,
				null,
				ImageColumns.BUCKET_DISPLAY_NAME,
				null
			)
				?.use { cursor ->
					val buffer: MutableList<BucketEntry?> = mutableListOf(null)
					while (cursor.moveToNext()) {
						val entry = BucketEntry(
							cursor.getInt(0),
							cursor.getString(1)
						)
						if (!buffer.contains(entry))
							buffer.add(entry)
					}

					buffer
				} ?: emptyList<BucketEntry>()

			emit(bucketEntries to lastSelectedBucketId)
		}

	private val inputSelectBucket = MutableLiveData<BucketEntry?>()

	@SuppressLint("InlinedApi")
	val outputImages: LiveData<PagedList<Uri>> = inputSelectBucket
		.switchMap { bucketEntry ->
			savedState[BucketEntry::id.name] = bucketEntry?.id
			object : DataSource.Factory<Int, Uri>() {
				override fun create(): DataSource<Int, Uri> =
					object : PositionalDataSource<Uri>() {
						override fun loadInitial(
							params: LoadInitialParams,
							callback: LoadInitialCallback<Uri>
						) {
							val projections = arrayOf("COUNT(${ImageColumns._ID})")
							val selection: String?
							val selectionArgs: Array<String>?
							when (bucketEntry) {
								null -> {
									selection = null
									selectionArgs = null
								}
								else -> {
									selection = "${ImageColumns.BUCKET_ID} = ?"
									selectionArgs = arrayOf("${bucketEntry.id}")
								}
							}

							val totalCount = when (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
								true -> context.contentResolver.query(
									MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
									projections,
									bundleOf(
										ContentResolver.QUERY_ARG_SQL_SELECTION to selection,
										ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS to selectionArgs,
									),
									null
								)
								false -> context.contentResolver.query(
									MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
									projections,
									selection,
									selectionArgs,
									null
								)
							}?.use {
								it.moveToFirst()
								it.getIntOrNull(0)
							}
							val list =
								fetch(params.requestedLoadSize, params.requestedStartPosition)
							when (totalCount) {
								null -> callback.onResult(list, 0)
								else -> callback.onResult(list, 0, totalCount)
							}
						}

						override fun loadRange(
							params: LoadRangeParams,
							callback: LoadRangeCallback<Uri>
						) {
							callback.onResult(fetch(params.loadSize, params.startPosition))
						}

						fun fetch(limit: Int, offset: Int): MutableList<Uri> {
							val result = mutableListOf<Uri>()

							val projections = arrayOf(ImageColumns._ID)
							val selection: String?
							val selectionArgs: Array<String>?
							when (bucketEntry) {
								null -> {
									selection = null
									selectionArgs = null
								}
								else -> {
									selection = "${ImageColumns.BUCKET_ID} = ?"
									selectionArgs = arrayOf("${bucketEntry.id}")
								}
							}

							when (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
								true -> context.contentResolver.query(
									MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
									projections,
									bundleOf(
										ContentResolver.QUERY_ARG_SQL_SELECTION to selection,
										ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS to selectionArgs,
										ContentResolver.QUERY_ARG_SORT_COLUMNS to arrayOf(
											ImageColumns.DATE_TAKEN
										),
										ContentResolver.QUERY_ARG_SORT_DIRECTION to ContentResolver.QUERY_SORT_DIRECTION_DESCENDING,
										ContentResolver.QUERY_ARG_LIMIT to limit,
										ContentResolver.QUERY_ARG_OFFSET to offset,
									),
									null
								)
								false -> context.contentResolver.query(
									MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
									projections,
									selection,
									selectionArgs,
									"${ImageColumns.DATE_TAKEN} DESC LIMIT $limit OFFSET $offset "
								)
							}?.use { c ->
								while (c.moveToNext()) {
									result.add(
										ContentUris.withAppendedId(
											MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
											c.getLong(c.getColumnIndexOrThrow(ImageColumns._ID))
										)
									)
								}
								result
							}
							return result
						}
					}
			}
				.toLiveData(Config(3 * 6))
		}

	fun selectBucket(bucketEntry: BucketEntry?) {
		inputSelectBucket.postValue(bucketEntry)
	}

	private val _selectedUris: MutableLiveData<MutableList<Uri>> = MutableLiveData()
	val selectedUris: LiveData<out List<Uri>> = _selectedUris

	fun clearSelection() {
		val value = _selectedUris.value
		if (!value.isNullOrEmpty()) {
			value.clear()
			_selectedUris.value = value
		}
	}

	fun toggleSelection(uri: Uri) {
		val list = _selectedUris.value ?: mutableListOf()
		if (!list.remove(uri)) {
			if (list.size >= args.purpose.maxImageCount)
				return
			list.add(uri)
		}
		_selectedUris.value = list
	}

	fun deleteSelection(uri: Uri) {
		val list = _selectedUris.value ?: return
		if (list.remove(uri))
			_selectedUris.value = list
	}
}