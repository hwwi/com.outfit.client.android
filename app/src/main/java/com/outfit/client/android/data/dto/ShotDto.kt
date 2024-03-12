package com.outfit.client.android.data.dto

import com.outfit.client.android.data.IDto
import java.util.*

data class ShotDto(
	override val id: Long,
	val caption: String,
	val images: List<ImageDto>,
	val likesCount: Int,
	val commentsCount: Int,
	val isPrivate: Boolean,
	val isViewerLike: Boolean,
	val isViewerBookmark: Boolean,
	val person: PersonDto,
	val createdAt: Date,
	val updatedAt: Date? ,
	val bookmarkedAt: Date?
) : IDto<Long>