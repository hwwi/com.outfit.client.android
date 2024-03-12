package com.outfit.client.android.data.args

data class ShotPostArgs(
	val caption: String,
	val tagListAndFileIndexList: List<TagListAndFileIndex>
)


data class TagListAndFileIndex(
	val fileIndex: Int,
	val itemTags: List<ItemTagArgs>
)