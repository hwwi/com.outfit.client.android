package com.outfit.client.android.data.payload

data class LikeShotPayload(
	val shotId: Long,
	val likesCount: Int,
	val isViewerLike: Boolean
)