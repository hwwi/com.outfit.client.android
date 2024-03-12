package com.outfit.client.android.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@Entity(
	tableName = "shot_bookmark",
	foreignKeys = [
		ForeignKey(
			entity = Shot::class,
			parentColumns = arrayOf("id"),
			childColumns = arrayOf("shot_id"),
			onDelete = ForeignKey.CASCADE,
			onUpdate = ForeignKey.CASCADE
		)
	]
)
data class ShotBookmark(
	@PrimaryKey @ColumnInfo(name = "shot_id") val shotId: Long,
	@ColumnInfo(name = "bookmarked_at") val bookmarkedAt: Date
) : Parcelable