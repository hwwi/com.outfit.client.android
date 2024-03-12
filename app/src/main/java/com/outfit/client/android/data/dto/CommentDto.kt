package com.outfit.client.android.data.dto

import com.outfit.client.android.data.IDto
import java.util.*

data class CommentDto(
	override val id: Long,
	val text: String,
	val likesCount: Int,
	val isViewerLike: Boolean,
	val shotId: Long,
	val person: PersonDto,
	val parentId: Long?,
	val createdAt: Date,
	val updatedAt: Date? = null
) : IDto<Long>