package com.outfit.client.android.widget.item

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.epoxy.AfterPropsSet
import com.airbnb.epoxy.CallbackProp
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.outfit.client.android.R
import kotlinx.android.synthetic.main.item_item_tag.view.*

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class ItemTagItem @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

	init {
		inflate(context, R.layout.item_item_tag, this)
	}

	@set:ModelProp
	lateinit var brandCode: String

	@set:ModelProp
	var productCode: String? = null


	@JvmOverloads
	@CallbackProp
	fun setOnTagClickListener(onTagClickListener: OnClickListener? = null) {
		setOnClickListener(onTagClickListener)
	}

	@AfterPropsSet
	fun afterPropsSet() {
		item_item_tag_text_first_char.text = brandCode.first().toString()
		item_item_tag_text_tag.text = when {
			productCode.isNullOrBlank() ->
				context.getString(R.string.tag_brand, brandCode)
			else ->
				context.getString(R.string.tag_product, brandCode, productCode)

		}
	}
}