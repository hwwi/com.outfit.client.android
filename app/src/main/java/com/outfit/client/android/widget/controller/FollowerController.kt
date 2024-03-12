package com.outfit.client.android.widget.controller

import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.paging.PagedListEpoxyController
import com.outfit.client.android.data.dto.PersonDto
import com.outfit.client.android.widget.item.FollowerItemModel_

class FollowerController(
	private val onPersonClick: (PersonDto) -> Unit
) : PagedListEpoxyController<PersonDto>() {
	override fun buildItemModel(currentPosition: Int, item: PersonDto?): EpoxyModel<*> {
		item!!
		return FollowerItemModel_().apply {
			id(item.id)
			person(item)
			onPersonClickListener { model, _, _, _ ->
				onPersonClick(model.person())
			}
			//TODO 구현할까??
			isUnfollowButtonVisible(false)
			isFollowingButtonVisible(false)
		}
	}
}