package com.outfit.client.android.data.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.outfit.client.android.data.IEntity
import com.outfit.client.android.data.NotificationType
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@Entity(tableName = "notification")
data class Notification(
	@PrimaryKey override val id: Long,
	@ColumnInfo(name = "type") val type: NotificationType,
	@ColumnInfo(name = "shot_id") val shotId: Long?,
	// Type in (ShotIncludePersonTag, ShotLiked) => not null
	@ColumnInfo(name = "shot_preview_image_url") val shotPreviewImageUrl: String?,
	@ColumnInfo(name = "comment_id") val commentId: Long?,
	// Type in (Commented, CommentIncludePersonTag, CommentLiked) => not null
	@ColumnInfo(name = "comment_text") val commentText: String?,
	@Embedded(prefix = "producer_") val producer: Person,
	@ColumnInfo(name = "created_at") val createdAt: Date
) : IEntity<Long>