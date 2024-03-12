package com.outfit.client.android.ui.selectimage.editimage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.outfit.client.android.data.AspectRatio
import com.outfit.client.android.di.AssistedArgsViewModelFactory
import com.outfit.client.android.glide.AspectRatioCrop
import com.outfit.client.android.glide.GlideApp
import com.outfit.client.android.glide.RectCrop
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class EditImageViewModel @AssistedInject constructor(
	@Assisted private val args: EditImageFragmentArgs,
	private val context: Context
) : ViewModel() {

	@AssistedInject.Factory
	interface Factory : AssistedArgsViewModelFactory<EditImageFragmentArgs>

	private val _selectedUri = MutableLiveData<Pair<Uri, Rect?>>(args.uris[0] to null)
	val selectedUriAndCropRect: LiveData<Pair<Uri, Rect?>> = _selectedUri

	private val _currentAspectRatio = MutableLiveData<AspectRatio>(args.purpose.defaultRatio)
	val currentAspectRatio: LiveData<AspectRatio> = _currentAspectRatio

	private val _uriPerCroppedRect = mutableMapOf<Uri, Rect?>()

	fun select(uri: Uri) {
		_selectedUri.value = uri to _uriPerCroppedRect[uri]
	}

	fun select(aspectRatio: AspectRatio) {
		if (_currentAspectRatio.value == aspectRatio) return
		_uriPerCroppedRect.clear()
		_currentAspectRatio.value = aspectRatio
	}

	fun updateCropRect(rect: Rect) {
		val uri = _selectedUri.value?.first ?: return
		_uriPerCroppedRect[uri] = rect
	}

	suspend fun getSelectedCroppedFiles(): List<File> =
		withContext(Dispatchers.IO) {
			args.uris.map { uri ->
				val rect = _uriPerCroppedRect[uri]
				val bitmap = GlideApp.with(context)
					.asBitmap()
					.load(uri)
					.transform(
						rect?.let { RectCrop(it) }
							?: AspectRatioCrop(
								args.purpose.defaultRatio.x,
								args.purpose.defaultRatio.y
							)
					)
					.submit()
					.get()

				val destCacheFile = createTempFile(suffix = ".jpeg", directory = context.cacheDir)
				destCacheFile.outputStream().use { output ->
					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output)
				}
				destCacheFile
			}
		}
}