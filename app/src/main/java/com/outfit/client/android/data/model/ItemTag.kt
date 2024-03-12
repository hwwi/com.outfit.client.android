package com.outfit.client.android.data.model

import android.os.Parcelable
import com.outfit.client.android.data.vo.TagDisplayable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ItemTag(
	override val brand: String,
	override val product: String,
	override val x: Float,
	override val y: Float
) : Parcelable, TagDisplayable