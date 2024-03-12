package com.outfit.client.android.widget.item

import android.content.Context
import android.text.format.DateUtils
import android.text.method.LinkMovementMethod
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.epoxy.*
import com.airbnb.epoxy.preload.ViewData
import com.airbnb.epoxy.preload.ViewMetadata
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.outfit.client.android.R
import com.outfit.client.android.data.model.Shot
import com.outfit.client.android.data.vo.TagDisplayable
import com.outfit.client.android.glide.GlideApp
import com.outfit.client.android.text.style.OnTagSpansClickListener
import com.outfit.client.android.text.style.setTagSpans
import com.outfit.client.android.widget.controller.ImageAndItemTagsListController
import kotlinx.android.synthetic.main.fragment_view_shot.*
import kotlinx.android.synthetic.main.item_timeline.view.*

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class TimelineItem @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

	private val controller = ImageAndItemTagsListController().apply {
		onImageClickListener = {
			isTagVisible = !isTagVisible
			requestModelBuild()
		}
		onModelBuild = {
			item_timeline_button_tag_visible.setImageResource(
				when (isTagVisible) {
					true -> R.drawable.ic_tag
					false -> R.drawable.ic_tag_off
				}
			)
		}
	}

	init {
		inflate(context, R.layout.item_timeline, this)
		item_timeline_adapter_indicator.attachTo(item_timeline_list_photo)
		item_timeline_list_photo.apply {
			isNestedScrollingEnabled = false
			setController(controller)
			addGlidePreloader(
				controller,
				GlideApp.with(this@TimelineItem),
				preloader = glidePreloader { requestManager: RequestManager, epoxyModel: TagableImageItemModel_, _: ViewData<ViewMetadata?> ->
					requestManager.load(epoxyModel.uri())
				}
			)
		}
		item_timeline_button_tag_visible.setOnClickListener {
			controller.onImageClickListener?.invoke()
		}
		item_timeline_text_caption.movementMethod = LinkMovementMethod.getInstance()
	}

	@set:CallbackProp
	var onCaptionTagSpansClickListener: OnTagSpansClickListener? = null

	@JvmOverloads
	@CallbackProp
	fun onPersonNameClickListener(onClickListener: OnClickListener? = null) {
		item_timeline_text_person_name.setOnClickListener(onClickListener)
	}

	@JvmOverloads
	@CallbackProp
	fun onMenuButtonClickListener(onClickListener: OnClickListener? = null) {
		item_timeline_button_menu.setOnClickListener(onClickListener)
	}

	@JvmOverloads
	@CallbackProp
	fun onLikeButtonClickListener(onClickListener: OnClickListener? = null) {
		item_timeline_button_like.setOnClickListener(onClickListener)
	}

	@JvmOverloads
	@CallbackProp
	fun onCommentButtonClickListener(onClickListener: OnClickListener? = null) {
		item_timeline_button_comment.setOnClickListener(onClickListener)
	}

	@JvmOverloads
	@CallbackProp
	fun onBookmarkButtonClickListener(onClickListener: OnClickListener? = null) {
		item_timeline_button_bookmark.setOnClickListener(onClickListener)
	}

	@JvmOverloads
	@CallbackProp
	fun onTagClickListener(onTagClickListener: ((View, TagDisplayable) -> Unit)? = null) {
		controller.onTagClickListener = onTagClickListener
	}

	@JvmOverloads
	@ModelProp
	fun setBookmarkButtonVisibility(visibility: Int = View.VISIBLE) {
		item_timeline_button_bookmark.visibility = visibility
	}

	@JvmOverloads
	@ModelProp
	fun setMenuButtonVisibility(visibility: Int = View.VISIBLE) {
		item_timeline_button_menu.visibility = visibility
	}

	@JvmOverloads
	@ModelProp
	fun setCommentButtonVisibility(visibility: Int = View.VISIBLE) {
		item_timeline_button_comment.visibility = visibility
	}

	@TextProp
	fun setCommentButtonText(text: CharSequence?) {
		item_timeline_button_comment.text = text
	}

	@ModelProp
	fun shot(shot: Shot) {
		GlideApp.with(this)
			.load(shot.person.profileImageUrl)
			.apply(RequestOptions.circleCropTransform())
			.fallback(R.drawable.ic_person_24dp)
			.error(R.drawable.ic_error_outline_24dp)
			.into(item_timeline_image_person_profile)
		item_timeline_text_person_name.text = shot.person.name

		controller.images = shot.images
		controller.requestModelBuild()
		when {
			shot.caption.isBlank() -> item_timeline_text_caption.visibility = GONE
			else ->
				item_timeline_text_caption.text =
					shot.caption.setTagSpans(onCaptionTagSpansClickListener)
		}


		item_timeline_button_like.setImageResource(shot.likeResourceId)
		item_timeline_button_bookmark.setImageResource(shot.bookmarkResourceId)
		item_timeline_text_like_count.text =
			context.getString(R.string.str_d_liked, shot.likesCount)
		item_timeline_text_comment_count.text =
			context.getString(R.string.str_d_comments, shot.commentsCount)

		item_timeline_text_created_at.text =
			DateUtils.getRelativeTimeSpanString(shot.createdAt.time)
	}

}