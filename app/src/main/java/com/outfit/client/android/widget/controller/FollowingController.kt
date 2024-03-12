package com.outfit.client.android.widget.controller

import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.paging.PagedListEpoxyController
import com.outfit.client.android.data.dto.PersonDto
import com.outfit.client.android.widget.item.FollowingItemModel_

class FollowingController(
	private val isUnfollowButtonVisible: Boolean,
	private val onPersonClick: (PersonDto) -> Unit,
	private val onUnfollowButtonClickListener: (PersonDto) -> Unit
) : PagedListEpoxyController<PersonDto>() {
	override fun buildItemModel(currentPosition: Int, item: PersonDto?): EpoxyModel<*> {
		item!!
		return FollowingItemModel_().apply {
			id(item.id)
			person(item)
			onPersonClickListener { model, _, _, _ ->
				onPersonClick(model.person())
			}
			onUnfollowButtonClickListener { model, _, _, _ ->
				onUnfollowButtonClickListener(model.person())
			}
			isUnfollowButtonVisible(this@FollowingController.isUnfollowButtonVisible)
		}
	}
}