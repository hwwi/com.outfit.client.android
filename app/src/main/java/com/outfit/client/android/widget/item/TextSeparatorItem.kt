package com.outfit.client.android.widget.item

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import com.airbnb.epoxy.AfterPropsSet
import com.airbnb.epoxy.CallbackProp
import com.airbnb.epoxy.ModelView
import com.airbnb.epoxy.TextProp
import com.outfit.client.android.R
import kotlinx.android.synthetic.main.item_text_separator.view.*

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class TextSeparatorItem @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

	init {
		inflate(context, R.layout.item_text_separator, this)
		gravity = Gravity.CENTER
	}

	@set:CallbackProp
	var onTextSeparatorClickListener: (() -> Unit)? = null

	@set:TextProp
	var text: CharSequence? = null

	@AfterPropsSet
	fun afterPropsSet() {
		text_separator_text.text = text
		setOnClickListener {
			onTextSeparatorClickListener?.invoke()
		}
	}

}