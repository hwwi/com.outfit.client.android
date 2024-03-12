package com.outfit.client.android.data.args

import android.os.Parcelable
import com.outfit.client.android.data.vo.TagDisplayable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ItemTagArgs(
	override val brand: String,
	override val product: String,
	override val x: Float,
	override val y: Float
) : TagDisplayable, Parcelable