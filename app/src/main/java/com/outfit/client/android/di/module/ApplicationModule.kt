package com.outfit.client.android.di.module

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [ApplicationModule.ProvidesModule::class])
interface ApplicationModule {

	@Module
	class ProvidesModule {
		@Singleton
		@Provides
		fun provideFirebaseAnalytics(): FirebaseAnalytics = Firebase.analytics
	}
}