package com.outfit.client.android.widget.controller

import android.view.View
import androidx.core.net.toUri
import com.airbnb.epoxy.TypedEpoxyController
import com.outfit.client.android.data.vo.TagDisplayable
import com.outfit.client.android.widget.item.tagableImageItem
import java.io.File

class FilesTagableImageController<T : TagDisplayable>(
	private val files: Collection<File>
) : TypedEpoxyController<Map<File, List<T>>>() {
	var onTagAdd: ((File, x: Float, y: Float) -> Unit)? =
		null
	var onTagRemove: ((File, T) -> Unit)? = null
	var onThumbnailClickListener: ((index: Int) -> Unit)? = null


	override fun buildModels(
		filePerTagList: Map<File, List<T>>?
	) {
		files.forEachIndexed { index, file ->
			tagableImageItem {
				id(index)
				uri(file.toUri())
				tagList(filePerTagList?.get(file))
				onThumbnailClickListener(View.OnClickListener {
					onThumbnailClickListener?.invoke(index)
				})
				onImageClickListener { _: View, x: Float, y: Float ->
					onTagAdd?.invoke(file, x, y)
				}

				onTagCloseClickListener { _, tag ->
					onTagRemove?.invoke(file, tag as T)
				}
			}
		}
	}
}