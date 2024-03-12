package com.outfit.client.android.data.dto

import com.outfit.client.android.data.vo.TagDisplayable

data class ItemTagDto(
	override val brand: String,
	override val product: String,
	override val x: Float,
	override val y: Float
) : TagDisplayable