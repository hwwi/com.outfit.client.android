package com.outfit.client.android.widget.item

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.epoxy.CallbackProp
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.airbnb.epoxy.TextProp
import com.outfit.client.android.R
import com.outfit.client.android.databinding.ItemRetryBinding
import com.outfit.client.android.extension.getColorFromAttr
import com.outfit.client.android.extension.getString

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class RetryItem @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
	private val binding = ItemRetryBinding.inflate(LayoutInflater.from(context), this)

	init {
		setBackgroundColor(context.getColorFromAttr(R.attr.colorSurface))
	}

	@CallbackProp
	fun setOnRetryButtonClickListener(l: OnClickListener?) {
		binding.retryButton.setOnClickListener(l)
	}

	@TextProp
	fun setMessage(message: CharSequence) {
		binding.message.text = message
	}
}