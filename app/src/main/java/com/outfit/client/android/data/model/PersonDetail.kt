package com.outfit.client.android.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.outfit.client.android.data.IEntity
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@Entity(tableName = "person_detail")
data class PersonDetail(
	@PrimaryKey override val id: Long,
	@ColumnInfo(name = "name") val name: String,
	@ColumnInfo(name = "profile_image_url") val profileImageUrl: String?,
	@ColumnInfo(name = "closet_background_image_url") val closetBackgroundImageUrl: String?,
	@ColumnInfo(name = "biography") val biography: String,
	@ColumnInfo(name = "shots_count") val shotsCount: Int,
	@ColumnInfo(name = "followers_count") val followersCount: Int,
	@ColumnInfo(name = "followings_count") val followingsCount: Int,
	@ColumnInfo(name = "is_viewer_follow") val isViewerFollow: Boolean?,
	@ColumnInfo(name = "created_at") val createdAt: Date,
	@ColumnInfo(name = "updated_at") val updatedAt: Date?
) : IEntity<Long>