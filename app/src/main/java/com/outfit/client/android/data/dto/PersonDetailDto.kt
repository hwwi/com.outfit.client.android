package com.outfit.client.android.data.dto

import com.outfit.client.android.data.IDto
import java.util.*

data class PersonDetailDto(
	override val id: Long,
	val name: String,
	val biography: String,
	val profileImageUrl: String?,
	val closetBackgroundImageUrl: String?,
	val shotsCount: Int,
	val followersCount: Int,
	val followingsCount: Int,
	val isViewerFollow: Boolean?,
	val createdAt: Date,
	val updatedAt: Date?
) : IDto<Long>