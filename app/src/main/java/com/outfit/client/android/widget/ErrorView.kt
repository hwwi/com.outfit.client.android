package com.outfit.client.android.widget

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.annotation.StringRes
import com.outfit.client.android.databinding.ViewErrorBinding

class ErrorView @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
	private val binding = ViewErrorBinding.inflate(LayoutInflater.from(context), this)

	init {
		gravity = Gravity.CENTER
		orientation = VERTICAL
	}


	fun setError(@StringRes titleRes: Int?, @StringRes messageRes: Int?) {
		binding.textTitle.apply {
			if (titleRes == null) {
				text = null
				visibility = GONE
				return@apply
			}
			setText(titleRes)
			visibility = VISIBLE
		}
		binding.textMessage.apply {
			if (messageRes == null) {
				text = null
				visibility = GONE
				return@apply
			}
			setText(messageRes)
			visibility = VISIBLE
		}
	}

	fun clearError() {
		binding.textTitle.apply {
			text = null
			visibility = GONE
		}
		binding.textMessage.apply {
			text = null
			visibility = GONE
		}
	}
}