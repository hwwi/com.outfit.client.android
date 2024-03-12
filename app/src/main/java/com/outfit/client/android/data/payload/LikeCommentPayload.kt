package com.outfit.client.android.data.payload

data class LikeCommentPayload(
	val commentId: Long,
	val likesCount: Int,
	val isViewerLike: Boolean
)