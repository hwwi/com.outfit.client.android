package com.outfit.client.android.data.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.outfit.client.android.R
import com.outfit.client.android.data.IEntity
import com.outfit.client.android.data.IViewerLike
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@Entity(
	tableName = "shot"
)
data class Shot(
	@PrimaryKey override val id: Long,
	@ColumnInfo(name = "caption") val caption: String,
	@ColumnInfo(name = "images_json") val images: List<Image>,
	@ColumnInfo(name = "likes_count") val likesCount: Int,
	@ColumnInfo(name = "comments_count") val commentsCount: Int,
	@ColumnInfo(name = "is_private") val isPrivate: Boolean,
	@ColumnInfo(name = "is_viewer_like") override val isViewerLike: Boolean,
	@ColumnInfo(name = "is_viewer_bookmark") val isViewerBookmark: Boolean,
	@Embedded(prefix = "person_") val person: Person,
	@ColumnInfo(name = "created_at") val createdAt: Date,
	@ColumnInfo(name = "updated_at") val updatedAt: Date?
) : IEntity<Long>, IViewerLike {


	val bookmarkResourceId: Int
		get() = when (isViewerBookmark) {
			true -> R.drawable.ic_baseline_bookmark_24
			false -> R.drawable.ic_baseline_bookmark_border_24
		}
}