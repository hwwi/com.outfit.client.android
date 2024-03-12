package com.outfit.client.android.widget.item

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.epoxy.AfterPropsSet
import com.airbnb.epoxy.CallbackProp
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.outfit.client.android.R
import com.outfit.client.android.data.payload.SearchedHashTag
import kotlinx.android.synthetic.main.item_hash_tag.view.*

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class HashTagItem @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

	init {
		inflate(context, R.layout.item_hash_tag, this)
	}

	@set:ModelProp
	lateinit var hashTag: SearchedHashTag

	@set:CallbackProp
	var onClickListener: ((hashTag: SearchedHashTag) -> Unit)? = null

	@AfterPropsSet
	fun afterPropsSet() {
		item_hash_tag_text_first_char.text = hashTag.tag.first().toString()
		item_hash_tag_text_tag.text = hashTag.tag
		item_hash_tag_text_tag.setOnClickListener {
			onClickListener?.invoke(hashTag)
		}
	}
}