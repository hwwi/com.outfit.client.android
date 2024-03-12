package com.outfit.client.android.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.outfit.client.android.data.model.*
import com.outfit.client.android.db.dao.CommentDao
import com.outfit.client.android.db.dao.NotificationDao
import com.outfit.client.android.db.dao.PersonDetailDao
import com.outfit.client.android.db.dao.ShotDao

@Database(
	entities = [
		PersonDetail::class,
		Shot::class,
		ShotCacheId::class,
		ShotBookmark::class,
		Comment::class,
		Notification::class
	],
	version = 5,
	exportSchema = true
)
@TypeConverters(Converters::class)
abstract class NetworkCacheDatabase : RoomDatabase() {

	abstract fun personProfileDao(): PersonDetailDao

	abstract fun shotDao(): ShotDao

	abstract fun commentDao(): CommentDao

	abstract fun notificationDao(): NotificationDao
}