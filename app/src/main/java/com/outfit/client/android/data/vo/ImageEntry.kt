package com.outfit.client.android.data.vo

import android.graphics.Rect
import android.net.Uri

data class ImageEntry(
	val uri: Uri,
	val croppedRect: Rect?
)