package com.outfit.client.android.widget.item

import android.content.Context
import android.graphics.Color
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
class ProgressItem @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

	init {
		inflate(context, R.layout.item_progress, this)
	}
}