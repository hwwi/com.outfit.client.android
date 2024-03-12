package com.outfit.client.android.widget.item

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.epoxy.CallbackProp
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.bumptech.glide.request.RequestOptions
import com.outfit.client.android.R
import com.outfit.client.android.data.dto.PersonDto
import com.outfit.client.android.glide.GlideApp
import kotlinx.android.synthetic.main.item_follower.view.*

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class FollowerItem @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

	init {
		inflate(context, R.layout.item_follower, this)
	}

	@ModelProp
	fun person(person: PersonDto) {
		item_follower_text_name.text = context.getString(R.string.tag_person, person.name)
		GlideApp.with(this)
			.load(person.profileImageUrl)
			.apply(RequestOptions.circleCropTransform())
			.fallback(R.drawable.ic_person_24dp)
			.error(R.drawable.ic_error_outline_24dp)
			.into(item_follower_image_person_profile)
	}

	@JvmOverloads
	@CallbackProp
	fun onPersonClickListener(onClickListener: OnClickListener? = null) {
		item_follower_image_person_profile.setOnClickListener(onClickListener)
		item_follower_text_name.setOnClickListener(onClickListener)
	}

	@JvmOverloads
	@ModelProp
	fun isFollowingButtonVisible(isVisible: Boolean = true) {
		item_follower_button_following.visibility = when (isVisible) {
			true -> View.VISIBLE
			false -> View.GONE
		}
	}

	@JvmOverloads
	@CallbackProp
	fun onFollowingButtonClickListener(onClickListener: OnClickListener? = null) {
		item_follower_button_following.setOnClickListener(onClickListener)
	}

	@JvmOverloads
	@ModelProp
	fun isUnfollowButtonVisible(isVisible: Boolean = true) {
		item_follower_button_unfollow.visibility = when (isVisible) {
			true -> View.VISIBLE
			false -> View.GONE
		}
	}

	@JvmOverloads
	@CallbackProp
	fun onUnfollowButtonClickListener(onClickListener: OnClickListener? = null) {
		item_follower_button_unfollow.setOnClickListener(onClickListener)
	}
}