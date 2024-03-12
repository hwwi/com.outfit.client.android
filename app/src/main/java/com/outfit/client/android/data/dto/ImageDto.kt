package com.outfit.client.android.data.dto

data class ImageDto(
	val url: String,
	val contentType: String,
	val width: Int,
	val height: Int,
	val length: Long,
	val itemTags: List<ItemTagDto>
)