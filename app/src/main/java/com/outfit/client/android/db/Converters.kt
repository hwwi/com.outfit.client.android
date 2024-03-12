package com.outfit.client.android.db

import androidx.room.TypeConverter
import com.outfit.client.android.data.CacheType
import com.outfit.client.android.data.NotificationType
import com.outfit.client.android.data.model.Image
import com.squareup.moshi.JsonAdapter
import timber.log.Timber
import java.util.*


class Converters {
	companion object {
		lateinit var jsonAdapter: JsonAdapter<List<Image>>
	}

	@TypeConverter
	fun fromTimestamp(value: Long?): Date? =
		when (value) {
			null -> null
			else -> Date(value)
		}

	@TypeConverter
	fun dateToTimestamp(date: Date?): Long? =
		when (date) {
			null -> null
			else -> date.time
		}

	@TypeConverter
	fun stringToLongList(data: String?): List<Long>? {
		return data?.let {
			it.split(",").map {
				try {
					it.toLong()
				} catch (ex: NumberFormatException) {
					Timber.e(ex, "Cannot convert $it to number")
					null
				}
			}
		}?.filterNotNull()
	}

	@TypeConverter
	fun longListToString(longs: List<Long>?): String? {
		return longs?.joinToString(",")
	}


	@TypeConverter
	fun intToCacheType(ordinal: Int): CacheType? = CacheType.values()[ordinal]

	@TypeConverter
	fun cacheTypeToInt(enum: CacheType?): Int? = enum?.ordinal

	@TypeConverter
	fun intToNotificationType(ordinal: Int): NotificationType? = NotificationType.values()[ordinal]

	@TypeConverter
	fun notificationTypeToInt(enum: NotificationType?): Int? = enum?.ordinal

	@TypeConverter
	fun imagesToString(image: List<Image>?): String? = when (image) {
		null -> null
		else -> jsonAdapter.toJson(image)
	}

	@TypeConverter
	fun stringToImages(string: String?): List<Image>? = when (string) {
		null -> null
		else -> jsonAdapter.fromJson(string)
	}
}