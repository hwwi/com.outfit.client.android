package com.outfit.client.android.glide

import android.graphics.Bitmap
import android.graphics.Rect
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.util.Util
import java.nio.ByteBuffer
import java.security.MessageDigest

class RectCrop(val rect: Rect) : BitmapTransformation() {
	companion object {
		private const val ID = "com.outfit.client.android.glide.RectCrop"
		private val ID_BYTES: ByteArray = ID.toByteArray()
	}

	override fun transform(
		pool: BitmapPool,
		toTransform: Bitmap,
		outWidth: Int,
		outHeight: Int
	): Bitmap {
		return Bitmap.createBitmap(
			toTransform,
			rect.left,
			rect.top,
			rect.width(),
			rect.height()
		)
	}

	override fun updateDiskCacheKey(messageDigest: MessageDigest) {
		messageDigest.update(ID_BYTES)
		messageDigest.update(ByteBuffer.allocate(4).putInt(rect.left).array())
		messageDigest.update(ByteBuffer.allocate(4).putInt(rect.top).array())
		messageDigest.update(ByteBuffer.allocate(4).putInt(rect.right).array())
		messageDigest.update(ByteBuffer.allocate(4).putInt(rect.bottom).array())
	}

	override fun equals(other: Any?): Boolean {
		if (other !is RectCrop)
			return false
		return rect == other.rect
	}

	override fun hashCode(): Int =
		Util.hashCode(
			Util.hashCode(
				Util.hashCode(
					Util.hashCode(
						ID.hashCode(),
						Util.hashCode(rect.left)
					),
					Util.hashCode(rect.top)
				),
				Util.hashCode(rect.right)
			),
			Util.hashCode(rect.bottom)
		)


}