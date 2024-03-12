package com.outfit.client.android.widget.item

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.epoxy.CallbackProp
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.airbnb.epoxy.OnViewRecycled
import com.airbnb.epoxy.preload.Preloadable
import com.outfit.client.android.R
import com.outfit.client.android.data.vo.Ratio
import com.outfit.client.android.glide.GlideApp
import kotlinx.android.synthetic.main.item_thumbnail.view.*
import java.io.File


@ModelView(fullSpan = false)
class ThumbnailItem @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), Preloadable {

	init {
		inflate(context, R.layout.item_thumbnail, this)
	}

	@JvmOverloads
	@CallbackProp
	fun onThumbnailClickListener(onClickListener: OnClickListener? = null) {
		item_thumbnail_image_thumbnail.setOnClickListener(onClickListener)
	}

	@JvmOverloads
	@CallbackProp
	fun onCornerButtonClickListener(onClickListener: OnClickListener? = null) {
		item_thumbnail_button_corner.setOnClickListener(onClickListener)
	}

	@JvmOverloads
	@ModelProp
	fun selected(isSelected: Boolean = false) {
		when (isSelected) {
			true -> item_thumbnail_image_thumbnail.setColorFilter(Color.argb(180, 255, 255, 255))
			else -> item_thumbnail_image_thumbnail.clearColorFilter()
		}
	}

	@JvmOverloads
	@ModelProp
	fun selectedNumber(selectedNumber: Int? = null) {
		when (selectedNumber) {
			null -> {
				item_thumbnail_text_number.visibility = View.GONE
				item_thumbnail_text_number.background = null
				item_thumbnail_text_number.text = null
			}
			-1 -> {
				item_thumbnail_text_number.visibility = View.VISIBLE
				item_thumbnail_text_number.setBackgroundResource(R.drawable.item_thumbnail_check_false)

				item_thumbnail_text_number.text = null
			}
			else -> {
				item_thumbnail_text_number.visibility = View.VISIBLE
				item_thumbnail_text_number.setBackgroundResource(R.drawable.item_thumbnail_check_true)
				item_thumbnail_text_number.text = selectedNumber.toString()
			}
		}
	}

	@JvmOverloads
	@ModelProp
	fun cornerButtonIcon(iconRes: Int? = null) {
		when (iconRes) {
			null -> {
				item_thumbnail_button_corner.visibility = View.GONE
				item_thumbnail_button_corner.setImageDrawable(null)
			}
			else -> {
				item_thumbnail_button_corner.visibility = View.VISIBLE
				item_thumbnail_button_corner.setImageResource(iconRes)
			}
		}
	}


	@JvmOverloads
	@ModelProp
	fun dimensionRatio(ratio: Ratio = Ratio(1, 1)) {
		val layoutParams = item_thumbnail_image_thumbnail.layoutParams as LayoutParams
		layoutParams.dimensionRatio = "${ratio.x}:${ratio.y}"
	}

	@ModelProp
	fun image(file: File?) {
		if (file == null)
			return
		GlideApp.with(this)
			.load(file)
			.thumbnail(0.1f)
			.into(item_thumbnail_image_thumbnail)
	}

	@ModelProp
	fun image(uri: Uri?) {
		if (uri == null)
			return
		GlideApp.with(this)
			.load(uri)
			.thumbnail(0.1f)
			.into(item_thumbnail_image_thumbnail)
	}

	@OnViewRecycled
	fun onViewRecycled() {
		item_thumbnail_image_thumbnail.setImageDrawable(null)
	}

	override val viewsToPreload: List<View> = listOf(item_thumbnail_image_thumbnail)
}