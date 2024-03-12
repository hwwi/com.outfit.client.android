package com.outfit.client.android.glide

import android.graphics.Bitmap
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.util.Util
import java.nio.ByteBuffer
import java.security.MessageDigest
import kotlin.math.roundToInt

class AspectRatioCrop(val x: Int, val y: Int) : BitmapTransformation() {
	companion object {
		private const val ID = "com.outfit.client.android.glide.AspectRatioCrop"
		private val ID_BYTES: ByteArray = ID.toByteArray()
	}

	override fun transform(
		pool: BitmapPool,
		toTransform: Bitmap,
		outWidth: Int,
		outHeight: Int
	): Bitmap {
		val originalWidth: Int = toTransform.width
		val originalHeight: Int = toTransform.height
		val cropAspectRatio: Float = x / y.toFloat()
		val width: Int
		val height: Int

		when {
			originalWidth > originalHeight -> {
				height = (originalHeight * 0.8f).roundToInt()
				width = (height * cropAspectRatio).roundToInt()
			}
			else -> {
				width = (originalWidth * 0.8).roundToInt()
				height = (width / cropAspectRatio).roundToInt()
			}
		}

		return Bitmap.createBitmap(
			toTransform,
			(originalWidth - width) / 2,
			(originalHeight - height) / 2,
			width,
			height
		)
	}

	override fun updateDiskCacheKey(messageDigest: MessageDigest) {
		messageDigest.update(ID_BYTES)
		messageDigest.update(ByteBuffer.allocate(4).putInt(x).array())
		messageDigest.update(ByteBuffer.allocate(4).putInt(y).array())
	}

	override fun equals(other: Any?): Boolean {
		if (other !is AspectRatioCrop)
			return false
		return x == other.x && y == other.y
	}

	override fun hashCode(): Int =
		Util.hashCode(Util.hashCode(ID.hashCode(), Util.hashCode(x)), Util.hashCode(y))


}