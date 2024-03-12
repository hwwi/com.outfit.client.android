package com.outfit.client.android.widget.controller

import android.view.View
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.paging.PagedListEpoxyController
import com.outfit.client.android.R
import com.outfit.client.android.data.model.Shot
import com.outfit.client.android.data.vo.TagDisplayable
import com.outfit.client.android.text.style.OnTagSpansClickListener
import com.outfit.client.android.widget.item.TimelineItemModel_

class ShotTimelinePagedController(
	private val viewerId: Long
) : PagedListEpoxyController<Shot>() {
	var onPersonNameClickListener: ((personName: String) -> Unit)? = null
	var onMenuButtonClickListener: ((view: View, shot: Shot) -> Unit)? = null
	var onLikeButtonClickListener: ((view: View, shot: Shot) -> Unit)? = null
	var onCommentButtonClickListener: ((view: View, shot: Shot) -> Unit)? = null
	var onBookmarkButtonClickListener: ((view: View, shot: Shot) -> Unit)? = null
	var onTagClickListener: ((view: View, tag: TagDisplayable) -> Unit)? = null
	var onCaptionTagSpansClickListener: OnTagSpansClickListener? = null

	override fun buildItemModel(currentPosition: Int, item: Shot?): EpoxyModel<*> =
		when {
			item == null -> throw Error("Don't using placeholder")
			else -> TimelineItemModel_().apply {
				id(item.id)
				shot(item)
				when (item.person.id) {
					viewerId -> {
						menuButtonVisibility(View.VISIBLE)
						bookmarkButtonVisibility(View.GONE)
					}
					else -> {
						menuButtonVisibility(View.GONE)
						bookmarkButtonVisibility(View.VISIBLE)
					}
				}
				onPersonNameClickListener { model, _, _, _ ->
					onPersonNameClickListener?.invoke(model.shot().person.name)
				}
				onMenuButtonClickListener { model, _, clickedView, _ ->
					onMenuButtonClickListener?.invoke(clickedView, model.shot())
				}
				onLikeButtonClickListener { model, _, clickedView, _ ->
					onLikeButtonClickListener?.invoke(clickedView, model.shot())
				}
				onCommentButtonClickListener { model, _, clickedView, _ ->
					onCommentButtonClickListener?.invoke(clickedView, model.shot())
				}
				onBookmarkButtonClickListener { model, _, clickedView, _ ->
					onBookmarkButtonClickListener?.invoke(clickedView, model.shot())
				}
				onTagClickListener { view, tag ->
					onTagClickListener?.invoke(view, tag)
				}
				onCaptionTagSpansClickListener(onCaptionTagSpansClickListener)
				commentButtonText(R.string.msg_view_all_comment)
			}
		}
}