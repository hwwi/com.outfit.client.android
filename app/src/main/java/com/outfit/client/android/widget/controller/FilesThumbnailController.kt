package com.outfit.client.android.widget.controller

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.Typed2EpoxyController
import com.outfit.client.android.widget.item.thumbnailItem
import java.io.File

class FilesThumbnailController(
	val orientation: Int = RecyclerView.VERTICAL
) : Typed2EpoxyController<Collection<File>, Boolean>() {
	var onThumbnailClickListener: ((index: Int) -> Unit)? = null

	override fun buildModels(data1: Collection<File>?, isEnabled: Boolean) {
		data1?.forEachIndexed { index, file ->
			thumbnailItem {
				id(index)
				if (orientation == RecyclerView.HORIZONTAL)
					withWrapWidthMatchHeightLayout()
				image(file)
				onThumbnailClickListener(
					when (isEnabled) {
						true -> View.OnClickListener {
							onThumbnailClickListener?.invoke(index)
						}
						false -> null
					}
				)
			}
		}
	}
}