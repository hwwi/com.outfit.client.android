package com.outfit.client.android.data.dto

import com.outfit.client.android.data.IDto

data class PersonDto(
	override val id: Long,
	val name: String,
	val profileImageUrl: String?
) : IDto<Long>