package com.outfit.client.android.data.dto

data class Connection<T>(
	val edges: List<T>,
	val pageInfo: PageInfo
)