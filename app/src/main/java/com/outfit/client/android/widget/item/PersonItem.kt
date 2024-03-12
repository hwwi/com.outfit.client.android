package com.outfit.client.android.widget.item

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.epoxy.CallbackProp
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.bumptech.glide.request.RequestOptions
import com.outfit.client.android.R
import com.outfit.client.android.data.dto.PersonDto
import com.outfit.client.android.glide.GlideApp
import kotlinx.android.synthetic.main.item_person.view.*

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class PersonItem @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

	init {
		orientation = HORIZONTAL
		inflate(context, R.layout.item_person, this)
	}

	@ModelProp
	fun person(person: PersonDto) {
		item_person_text_tag.text = context.getString(R.string.tag_person, person.name)
		GlideApp.with(this)
			.load(person.profileImageUrl)
			.apply(RequestOptions.circleCropTransform())
			.fallback(R.drawable.ic_person_24dp)
			.error(R.drawable.ic_error_outline_24dp)
			.into(item_person_image_person_profile)
	}

	@JvmOverloads
	@CallbackProp
	fun onPersonNameClickListener(onClickListener: OnClickListener? = null) {
		item_person_text_tag.setOnClickListener(onClickListener)
	}
}