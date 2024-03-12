package com.outfit.client.android.util

import android.graphics.Bitmap
import android.graphics.BitmapRegionDecoder
import java.io.Closeable
import java.lang.ref.WeakReference

/*
 * Resources ->
 * @desc Closeable 객체들을 한 콜백 내에서 사용후 자동으로 close 되도록 하기 위해서 사용
 * hiup/speed
 */
class Resources : Closeable {
	private val resources = mutableListOf<WeakReference<out Any>>()

	fun <T : Closeable> T.use(): T {
		resources += WeakReference(this)
		return this
	}

	fun Bitmap.use(): Bitmap {
		resources += WeakReference(this)
		return this
	}

	fun BitmapRegionDecoder.use(): BitmapRegionDecoder {
		resources += WeakReference(this)
		return this
	}

	override fun close() {
		var exception: Exception? = null
		for (ref in resources.reversed()) {
			val resource = ref.get() ?: continue
			try {
				when (resource) {
					is Closeable -> resource.close()
					is BitmapRegionDecoder -> resource.recycle()
					is Bitmap -> resource.recycle()
				}
			} catch (closeException: Exception) {
				if (exception == null) {
					exception = closeException
				} else {
					exception.addSuppressed(closeException)
				}
			}
		}
		if (exception != null) throw exception
	}
}

inline fun <T> withResources(block: Resources.() -> T): T = Resources().use(block)
