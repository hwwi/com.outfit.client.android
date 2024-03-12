package com.outfit.client.android.data.dto

import com.outfit.client.android.data.NotificationType
import java.util.*

data class NotificationDto(
	val id: Long,
	val type: NotificationType,
	val shotId: Long?,
	// Type in (ShotIncludePersonTag, ShotLiked) => not null
	val shotPreviewImageUrl: String?,
	val commentId: Long?,
	// Type in (Commented, CommentIncludePersonTag, CommentLiked) => not null
	val commentText: String?,
	val producer: PersonDto,
	val createdAt: Date
)