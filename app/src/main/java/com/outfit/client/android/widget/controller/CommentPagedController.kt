package com.outfit.client.android.widget.controller

import android.view.View
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.paging.PagedListEpoxyController
import com.outfit.client.android.data.NetworkState
import com.outfit.client.android.data.model.Comment
import com.outfit.client.android.text.style.OnTagSpansClickListener
import com.outfit.client.android.widget.item.CommentItemModel_
import com.outfit.client.android.widget.item.NetworkStateItemModel_

class CommentPagedController(
	private val onCommentMenuClickListener: ((View, Comment) -> Unit)?,
	private val onCommentLikeClickListener: ((View, Comment) -> Unit)?,
	private val onCommentReplyClickListener: ((Comment) -> Unit)?,
	private val onRetryButtonClickListener: (() -> Unit)?,
	private val onTagSpansClickListener: OnTagSpansClickListener?
) : PagedListEpoxyController<Comment>() {
	var highlightCommentId: Long? = null
	var networkState: NetworkState<Unit>? = null
		set(value) {
			field = value
			requestModelBuild()
		}

	override fun addModels(models: List<EpoxyModel<*>>) {
		super.addModels(models)
		networkState?.let { networkState ->
			add(
				NetworkStateItemModel_().apply {
					id("networkState")
					networkState(networkState)
					onRetryButtonClickListener { _ ->
						onRetryButtonClickListener?.invoke()
					}
				}
			)
		}
	}

	override fun buildItemModel(currentPosition: Int, item: Comment?): EpoxyModel<*> =
		CommentItemModel_().apply {
			item!!
			id(item.id)
			isHighlight(item.id == highlightCommentId)
			comment(item)
			onTagSpansClickListener(onTagSpansClickListener)
			onMenuClickListener { model, _, clickedView, _ ->
				onCommentMenuClickListener?.invoke(clickedView, model.comment())
			}
			onLikeClickListener { model, _, clickedView, _ ->
				onCommentLikeClickListener?.invoke(clickedView, model.comment())
			}
			onReplyClickListener { model, _, clickedView, _ ->
				clickedView.requestFocus()
				onCommentReplyClickListener?.invoke(model.comment())
			}
		}
}