package com.outfit.client.android.widget

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.Carousel
import com.airbnb.epoxy.ModelView

@ModelView(saveViewState = true, autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class PagerCarousel @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyleAttr: Int = 0
) : Carousel(context, attrs, defStyleAttr) {

	override fun createLayoutManager(): LayoutManager =
		LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
}