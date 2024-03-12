package com.outfit.client.android.di.module

import android.content.Context
import androidx.room.Room
import com.outfit.client.android.db.NetworkCacheDatabase
import com.outfit.client.android.db.dao.CommentDao
import com.outfit.client.android.db.dao.NotificationDao
import com.outfit.client.android.db.dao.PersonDetailDao
import com.outfit.client.android.db.dao.ShotDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

	@Singleton
	@Provides
	fun provideNetworkCacheDatabase(
		context: Context
	): NetworkCacheDatabase {
		return Room
			.databaseBuilder(context, NetworkCacheDatabase::class.java, "network_cache.db")
			.fallbackToDestructiveMigration()
			.build()
	}

	@Singleton
	@Provides
	fun providePersonProfileDao(db: NetworkCacheDatabase): PersonDetailDao = db.personProfileDao()

	@Singleton
	@Provides
	fun provideShotDao(db: NetworkCacheDatabase): ShotDao = db.shotDao()

	@Singleton
	@Provides
	fun provideCommentDao(db: NetworkCacheDatabase): CommentDao = db.commentDao()

	@Singleton
	@Provides
	fun provideNotificationDao(db: NetworkCacheDatabase): NotificationDao = db.notificationDao()

}