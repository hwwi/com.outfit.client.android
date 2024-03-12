package com.outfit.client.android.widget.item

import android.content.Context
import android.text.format.DateUtils
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.epoxy.CallbackProp
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.airbnb.epoxy.preload.Preloadable
import com.bumptech.glide.request.RequestOptions
import com.outfit.client.android.R
import com.outfit.client.android.data.NotificationType
import com.outfit.client.android.data.model.Notification
import com.outfit.client.android.glide.GlideApp
import com.outfit.client.android.text.style.setTagSpans
import kotlinx.android.synthetic.main.item_notification.view.*


@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class NotificationItem @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), Preloadable {

	init {
		inflate(context, R.layout.item_notification, this)
	}

	@CallbackProp
	fun onClickListener(onClickListener: OnClickListener?) {
		setOnClickListener(onClickListener)
	}

	@CallbackProp
	fun onProducerProfileClickListener(onClickListener: OnClickListener?) {
		item_notification_image_producer_profile.setOnClickListener(onClickListener)
	}

	@CallbackProp
	fun onShotPreviewClickListener(onClickListener: OnClickListener?) {
		item_notification_image_shot_preview.setOnClickListener(onClickListener)
	}

	@CallbackProp
	fun onMenuButtonClickListener(onClickListener: OnClickListener?) {
		item_notification_button_menu.setOnClickListener(onClickListener)
	}

	@ModelProp
	fun notification(notification: Notification) {
		GlideApp.with(this)
			.load(notification.producer.profileImageUrl)
			.apply(RequestOptions.circleCropTransform())
			.fallback(R.drawable.ic_person_24dp)
			.error(R.drawable.ic_error_outline_24dp)
			.into(item_notification_image_producer_profile)

		when (val url = notification.shotPreviewImageUrl) {
			null -> {
				item_notification_image_shot_preview.visibility = GONE
				GlideApp.with(this).clear(item_notification_image_shot_preview)
			}
			else -> {
				item_notification_image_shot_preview.visibility = VISIBLE
				GlideApp.with(this)
					.load(url)
					.error(R.drawable.ic_error_outline_24dp)
					.into(item_notification_image_shot_preview)
			}
		}

		item_notification_text.text = when (notification.type) {
			NotificationType.ShotPosted -> context.getString(
				R.string.msg_shot_posted,
				notification.producer.name
			)
			NotificationType.ShotIncludePersonTag -> context.getString(
				R.string.msg_shot_include_person_tag,
				notification.producer.name
			)
			NotificationType.ShotLiked -> context.getString(
				R.string.msg_shot_liked,
				notification.producer.name
			)
			NotificationType.Commented -> context.getString(
				R.string.msg_commented,
				notification.producer.name,
				notification.commentText
			)
			NotificationType.CommentIncludePersonTag -> context.getString(
				R.string.msg_comment_include_person_tag,
				notification.producer.name,
				notification.commentText
			)
			NotificationType.CommentLiked -> context.getString(
				R.string.msg_comment_liked,
				notification.producer.name,
				notification.commentText
			)
			NotificationType.Followed -> context.getString(
				R.string.msg_followed,
				notification.producer.name
			)
		}
			.setTagSpans(null, notification.producer.name)

		item_notification_text_created_at.text =
			DateUtils.getRelativeTimeSpanString(notification.createdAt.time)
	}

	override val viewsToPreload: List<View> = listOf(item_notification_image_shot_preview)

}