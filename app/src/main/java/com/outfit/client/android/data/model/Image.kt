package com.outfit.client.android.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Image(
	val url: String,
	val contentType: String,
	val width: Int,
	val height: Int,
	val length: Long,
	val itemTags: List<ItemTag>
) : Parcelable