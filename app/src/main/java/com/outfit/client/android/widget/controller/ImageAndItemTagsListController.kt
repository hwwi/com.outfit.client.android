package com.outfit.client.android.widget.controller

import android.net.Uri
import android.view.View
import com.airbnb.epoxy.EpoxyController
import com.outfit.client.android.data.model.Image
import com.outfit.client.android.data.vo.Ratio
import com.outfit.client.android.data.vo.TagDisplayable
import com.outfit.client.android.widget.item.tagableImageItem

class ImageAndItemTagsListController : EpoxyController() {
	var onImageClickListener: (() -> Unit)? = null
	var onModelBuild: (() -> Unit)? = null
	var onTagClickListener: ((View, TagDisplayable) -> Unit)? = null

	var images: List<Image>? = null
	var isTagVisible: Boolean = true

	override fun buildModels() {
		onModelBuild?.invoke()
		images?.forEach {
			tagableImageItem {
				id(it.url)
				dimensionRatio(Ratio(it.width, it.height))
				uri(Uri.parse(it.url))
				tagList(it.itemTags)
				isTagVisible(isTagVisible)
				onImageClickListener { _, _, _ ->
					onImageClickListener?.invoke()
				}
				onTagClickListener(onTagClickListener)
			}
		}

	}

}