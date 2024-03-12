package com.outfit.client.android.widget.controller

import com.airbnb.epoxy.TypedEpoxyController
import com.outfit.client.android.R
import com.outfit.client.android.data.dto.PersonDto
import com.outfit.client.android.data.payload.SearchGetPayload
import com.outfit.client.android.data.payload.SearchedHashTag
import com.outfit.client.android.widget.item.hashTagItem
import com.outfit.client.android.widget.item.itemTagItem
import com.outfit.client.android.widget.item.personItem
import com.outfit.client.android.widget.item.textSeparatorItem

class SearchGetPayloadController(
	private val useCategoryTitle: Boolean = true
) : TypedEpoxyController<SearchGetPayload>() {

	var onPersonClickListener: ((PersonDto) -> Unit)? = null
	var onHashTagClickListener: ((SearchedHashTag) -> Unit)? = null
	var onItemTagClickListener: ((brandCode: String, productCode: String?) -> Unit)? = null

	override fun buildModels(data: SearchGetPayload?) {
		if (data == null)
			return

		if (data.persons != null) {
			if (useCategoryTitle)
				textSeparatorItem {
					id("closet")
					text(R.string.str_closet)
				}
			data.persons.forEach {
				personItem {
					id(it.toString())
					person(it)
					onPersonNameClickListener { model, _, _, _ ->
						onPersonClickListener?.invoke(model.person())
					}
				}
			}
		}
		if (data.hashTags != null) {
			if (useCategoryTitle)
				textSeparatorItem {
					id("hashTag")
					text(R.string.str_hash_tag)
				}
			data.hashTags.forEach {
				hashTagItem {
					id(it.toString())
					hashTag(it)
					onClickListener(onHashTagClickListener)
				}
			}
		}
		if (data.itemTags != null) {
			if (useCategoryTitle)
				textSeparatorItem {
					id("itemTag")
					text(R.string.str_brand)
				}

			data.itemTags.forEach { itemTag ->
				when {
					itemTag.productCodes.isNullOrEmpty() -> itemTagItem {
						id(itemTag.brandCode)
						brandCode(itemTag.brandCode)
						onTagClickListener { model, _, _, _ ->
							onItemTagClickListener?.invoke(model.brandCode(), null)
						}
					}
					else -> itemTag.productCodes.forEach { productCode ->
						itemTagItem {
							id(itemTag.brandCode + productCode)
							brandCode(itemTag.brandCode)
							productCode(productCode)
							onTagClickListener { model, _, _, _ ->
								onItemTagClickListener?.invoke(
									model.brandCode(),
									model.productCode()
								)
							}
						}
					}
				}

			}
		}
	}
}
