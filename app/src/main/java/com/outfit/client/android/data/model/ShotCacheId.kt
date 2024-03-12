package com.outfit.client.android.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import com.outfit.client.android.data.CacheType
import com.outfit.client.android.data.IIdentifiability
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(
	tableName = "shot_cache_id",
	primaryKeys = ["shot_id", "cache_type"],
	foreignKeys = [
		ForeignKey(
			entity = Shot::class,
			parentColumns = ["id"],
			childColumns = ["shot_id"],
			onDelete = ForeignKey.CASCADE,
			onUpdate = ForeignKey.CASCADE
		)
	]
)
data class ShotCacheId(
	@ColumnInfo(name = "shot_id") val shotId: Long,
	@ColumnInfo(name = "cache_type") val cacheType: CacheType
) : Parcelable