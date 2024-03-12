package com.outfit.client.android.ui.selectimage.editimage

import android.net.Uri
import com.airbnb.epoxy.TypedEpoxyController
import com.outfit.client.android.widget.item.thumbnailItem

class PreviewController : TypedEpoxyController<Array<Uri>>() {
	var onThumbnailClickListener: ((Uri) -> Unit)? = null

	override fun buildModels(data: Array<Uri>?) {
		data?.forEach { item ->
			thumbnailItem {
				id(item.hashCode())
				withWrapWidthMatchHeightLayout()
				image(item)
				onThumbnailClickListener { _ ->
					onThumbnailClickListener?.invoke(item)
				}
			}
		}
	}
}