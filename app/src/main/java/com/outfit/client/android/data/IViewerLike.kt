package com.outfit.client.android.data

import com.outfit.client.android.R

interface IViewerLike {
	val isViewerLike: Boolean

	val likeResourceId: Int
		get() = when {
			isViewerLike -> R.drawable.ic_heart
			else -> R.drawable.ic_heart_outline
		}
}