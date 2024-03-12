package com.outfit.client.android.data.dto

data class PageInfo(
	val startCursor: String?,
	val endCursor: String?,
	val hasMorePage: Boolean
)