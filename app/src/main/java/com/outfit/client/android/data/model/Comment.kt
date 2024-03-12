package com.outfit.client.android.data.model

import androidx.room.*
import com.outfit.client.android.data.IEntity
import com.outfit.client.android.data.IViewerLike
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@Entity(
	tableName = "comment",
	foreignKeys = [
		ForeignKey(
			entity = Shot::class,
			parentColumns = ["id"],
			childColumns = ["shot_id"],
			onDelete = ForeignKey.CASCADE,
			onUpdate = ForeignKey.CASCADE
		),
		ForeignKey(
			entity = Comment::class,
			parentColumns = ["id"],
			childColumns = ["parent_id"],
			onDelete = ForeignKey.CASCADE,
			onUpdate = ForeignKey.CASCADE
		)
	],
	indices = [
		Index("shot_id"),
		Index("parent_id")
	]
)
data class Comment(
	@PrimaryKey override val id: Long,
	@ColumnInfo(name = "text") val text: String,
	@ColumnInfo(name = "likes_count") val likesCount: Int,
	@ColumnInfo(name = "is_viewer_like") override val isViewerLike: Boolean,
	@ColumnInfo(name = "shot_id") val shotId: Long,
	@Embedded(prefix = "person_") val person: Person,
	@ColumnInfo(name = "parent_id") val parentId: Long?,
	@ColumnInfo(name = "created_at") val createdAt: Date,
	@ColumnInfo(name = "updated_at") val updatedAt: Date?
) : IEntity<Long>, IViewerLike