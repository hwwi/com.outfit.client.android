package com.outfit.client.android.data.payload

import com.outfit.client.android.data.dto.PersonDto

data class SearchGetPayload(
	val persons: List<PersonDto>?,
	val hashTags: List<SearchedHashTag>?,
	val itemTags: List<SearchedItemTag>?
)