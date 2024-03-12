package com.outfit.client.android.widget.item

import android.annotation.SuppressLint
import android.content.Context
import android.text.format.DateUtils
import android.text.method.LinkMovementMethod
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.epoxy.AfterPropsSet
import com.airbnb.epoxy.CallbackProp
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.airbnb.epoxy.preload.Preloadable
import com.bumptech.glide.request.RequestOptions
import com.outfit.client.android.R
import com.outfit.client.android.data.model.Comment
import com.outfit.client.android.glide.GlideApp
import com.outfit.client.android.text.style.OnTagSpansClickListener
import com.outfit.client.android.text.style.setTagSpans
import kotlinx.android.synthetic.main.item_comment.view.*


@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class CommentItem @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), Preloadable {

	init {
		inflate(context, R.layout.item_comment, this)
		text_comment_html.movementMethod = LinkMovementMethod.getInstance()
	}

	@set:CallbackProp
	var onTagSpansClickListener: OnTagSpansClickListener? = null

	@CallbackProp
	fun setOnMenuClickListener(onMenuClickListener: OnClickListener?) {
		button_comment_menu.setOnClickListener(onMenuClickListener)
	}

	@CallbackProp
	fun setOnLikeClickListener(onLikeClickListener: OnClickListener?) {
		button_comment_like.setOnClickListener(onLikeClickListener)
	}

	@CallbackProp
	fun setOnReplyClickListener(onReplyClickListener: OnClickListener?) {
		button_comment_reply.setOnClickListener(onReplyClickListener)
	}

	@ModelProp
	fun isHighlight(isHighlight: Boolean) {
		when {
			isHighlight -> setBackgroundResource(R.drawable.background_comment_highlight)
			else -> background = null
		}
	}

	@ModelProp
	lateinit var comment: Comment

	@SuppressLint("SetTextI18n")
	@AfterPropsSet
	fun afterPropsSet() {
		setPadding(
			when (comment.parentId) {
				null -> 0
				else -> TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP,
					12f,
					resources.displayMetrics
				).toInt()
			}, 0, 0, 0
		)
		GlideApp.with(this)
			.load(comment.person.profileImageUrl)
			.apply(RequestOptions.circleCropTransform())
			.fallback(R.drawable.ic_person_24dp)
			.error(R.drawable.ic_error_outline_24dp)
			.into(image_comment_person_profile)

		text_comment_html.text = "${comment.person.name} ${comment.text}"
			.setTagSpans(onTagSpansClickListener, comment.person.name)
		text_comment_created_at.text = DateUtils.getRelativeTimeSpanString(comment.createdAt.time)
		button_comment_like.text = when (comment.likesCount) {
			0 -> null
			else -> comment.likesCount.toString()
		}
		button_comment_like.setIconResource(comment.likeResourceId)
	}

	override val viewsToPreload: List<View> = listOf(image_comment_person_profile)

}