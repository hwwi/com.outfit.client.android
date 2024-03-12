package com.outfit.client.android.widget.controller

import android.net.Uri
import android.view.View
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.paging.PagedListEpoxyController
import com.outfit.client.android.data.model.Shot
import com.outfit.client.android.data.vo.Ratio
import com.outfit.client.android.widget.item.ThumbnailItemModel_

class ShotThumbnailPagedController(
	private val onThumbnailClickListener: (shot: Shot) -> Unit
) : PagedListEpoxyController<Shot>() {

	override fun buildItemModel(currentPosition: Int, item: Shot?): EpoxyModel<*> =
		when {
			item == null -> throw Error("Don't using placeholder")
			else -> ThumbnailItemModel_().apply {
				id(item.id)
				dimensionRatio(Ratio(1, 1))
				onThumbnailClickListener(View.OnClickListener {
					onThumbnailClickListener(item)
				})
				image(item.images.first().let { Uri.parse(it.url) })
			}
		}
}