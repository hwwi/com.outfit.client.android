package com.outfit.client.android.widget.item

import android.content.Context
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.epoxy.CallbackProp
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.airbnb.epoxy.preload.Preloadable
import com.google.android.material.chip.Chip
import com.outfit.client.android.R
import com.outfit.client.android.data.vo.Ratio
import com.outfit.client.android.data.vo.TagDisplayable
import com.outfit.client.android.glide.GlideApp
import kotlinx.android.synthetic.main.item_tagable_image.view.*

@ModelView(
	// 현재 뷰페이저 아이템으로만 사용해서 MATCH_WIDTH_MATCH_HEIGHT 적용
	autoLayout = ModelView.Size.MATCH_WIDTH_MATCH_HEIGHT,
	fullSpan = false
)
class TagableImageItem @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), Preloadable {

	init {
		inflate(context, R.layout.item_tagable_image, this)
		item_tagable_image_image_thumbnail.setOnTouchListener { _, event ->
			gestureDetector.onTouchEvent(event) || super.onTouchEvent(event)
		}
	}

	private val gestureDetector =
		GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
			override fun onDown(e: MotionEvent?): Boolean = true
			override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
				onThumbnailClickListener?.onClick(this@TagableImageItem)
				val onImageClickListener = onImageClickListener ?: return false
				val displayRect = displayRect ?: return false

				if (displayRect.contains(e.x, e.y)) {
					val x = (e.x - displayRect.left) / displayRect.width()
					val y = (e.y - displayRect.top) / displayRect.height()
					onImageClickListener(this@TagableImageItem, x, y)
					return true
				}
				return false
			}

			override fun onLongPress(e: MotionEvent?) {
//			onLongClickListener?.onLongClick(this@ThumbnailItem)
			}
		})

	private val displayRect: RectF?
		get() {
			val drawable: Drawable = item_tagable_image_image_thumbnail.drawable ?: return null
			val bounds = RectF()
			item_tagable_image_image_thumbnail.imageMatrix.mapRect(bounds, RectF(drawable.bounds))
			return bounds
		}

	@set:CallbackProp
	var onThumbnailClickListener: OnClickListener? = null

	@set:CallbackProp
	var onImageClickListener: ((view: View, x: Float, y: Float) -> Unit)? = null

	@set:CallbackProp
	var onTagClickListener: ((view: View, tag: TagDisplayable) -> Unit)? = null

	@set:CallbackProp
	var onTagCloseClickListener: ((view: View, tag: TagDisplayable) -> Unit)? = null

	@JvmOverloads
	@ModelProp
	fun dimensionRatio(ratio: Ratio? = null) {
		val layoutParams = item_tagable_image_image_thumbnail.layoutParams as LayoutParams
		when (ratio) {
			null -> {
				layoutParams.height = LayoutParams.WRAP_CONTENT
				layoutParams.dimensionRatio = null
			}
			else -> {
				layoutParams.height = 0
				layoutParams.dimensionRatio = "${ratio.x}:${ratio.y}"
			}
		}
	}

	@JvmOverloads
	@ModelProp
	fun uri(uri: Uri? = null) {
		when (uri) {
			null -> GlideApp.with(this)
				.clear(item_tagable_image_image_thumbnail)
			else -> GlideApp.with(this)
				.load(uri)
				.thumbnail(0.1f)
				.into(item_tagable_image_image_thumbnail)
		}
	}

	@JvmOverloads
	@ModelProp
	fun isTagVisible(isTagVisible: Boolean = true) {
		item_tagable_image_layout_chips.visibility = when (isTagVisible) {
			true -> View.VISIBLE
			false -> View.GONE
		}
	}

	@JvmOverloads
	@ModelProp
	fun tagList(tagList: List<TagDisplayable>? = null) {
		item_tagable_image_layout_chips.removeAllViews()
		tagList?.forEach { tag ->
			val chip = Chip(context).apply {
				layoutParams =
					LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
						horizontalBias = tag.x
						verticalBias = tag.y
						topToTop = LayoutParams.PARENT_ID
						bottomToBottom = LayoutParams.PARENT_ID
						startToStart = LayoutParams.PARENT_ID
						endToEnd = LayoutParams.PARENT_ID
					}
				setTextAppearanceResource(R.style.TextAppearance_Outfit_Caption)
				text = when {
					tag.product.isNullOrBlank() -> context.getString(R.string.tag_brand, tag.brand)
					else -> context.getString(R.string.tag_product, tag.brand, tag.product)
				}
				isCloseIconVisible = onTagCloseClickListener != null
				setOnClickListener {
					onTagClickListener?.invoke(it, tag)
				}
				setOnCloseIconClickListener {
					onTagCloseClickListener?.invoke(it, tag)
				}
				setTag(R.id.tag_tag, tag)
			}
			item_tagable_image_layout_chips.addView(chip)
		}
	}

	override val viewsToPreload: List<View> = listOf(item_tagable_image_image_thumbnail)
}