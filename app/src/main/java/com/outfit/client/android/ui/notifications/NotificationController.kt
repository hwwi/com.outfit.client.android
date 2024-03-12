package com.outfit.client.android.ui.notifications

import android.view.View
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.paging.PagedListEpoxyController
import com.outfit.client.android.data.model.Notification
import com.outfit.client.android.widget.item.NotificationItemModel_

class NotificationController(
	val onClickListener: (View, Notification) -> Unit,
	val onProducerProfileClickListener: (View, Notification) -> Unit,
	val onShotPreviewClickListener: (View, Notification) -> Unit,
	val onMenuButtonClickListener: (View, Notification) -> Unit
) : PagedListEpoxyController<Notification>() {
	override fun buildItemModel(currentPosition: Int, item: Notification?): EpoxyModel<*> =
		NotificationItemModel_().apply {
			item!!
			id(item.id)
			notification(item)
			onClickListener { model, _, clickedView, _ ->
				onClickListener(clickedView, model.notification())
			}
			onProducerProfileClickListener { model, _, clickedView, _ ->
				onProducerProfileClickListener(clickedView, model.notification())
			}
			onShotPreviewClickListener { model, _, clickedView, _ ->
				onShotPreviewClickListener(clickedView, model.notification())
			}
			onMenuButtonClickListener { model, _, clickedView, _ ->
				onMenuButtonClickListener(clickedView, model.notification())
			}
		}
}