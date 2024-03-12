package com.outfit.client.android.data.model

import androidx.room.ColumnInfo
import com.outfit.client.android.data.IEntity
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Person(
	@ColumnInfo(name = "id") override val id: Long,
	@ColumnInfo(name = "name") val name: String,
	@ColumnInfo(name = "profile_image_url") val profileImageUrl: String?
) : IEntity<Long>