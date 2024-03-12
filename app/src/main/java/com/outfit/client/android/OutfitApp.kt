package com.outfit.client.android

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import androidx.work.Configuration
import androidx.work.WorkManager
import com.airbnb.epoxy.Carousel
import com.facebook.stetho.Stetho
import com.facebook.stetho.timber.StethoTree
import com.google.firebase.iid.FirebaseInstanceId
import com.outfit.client.android.di.DaggerWorkerFactory
import com.outfit.client.android.di.component.DaggerApplicationComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import timber.log.Timber
import javax.inject.Inject


class OutfitApp : Application(), HasAndroidInjector {

	@Inject
	lateinit var androidInjector: DispatchingAndroidInjector<Any>

	@Inject
	lateinit var daggerWorkerFactory: DaggerWorkerFactory

	override fun androidInjector(): AndroidInjector<Any> = androidInjector

	override fun attachBaseContext(base: Context) {
		super.attachBaseContext(base)
		MultiDex.install(this)
		Carousel.setDefaultItemSpacingDp(0)
		DaggerApplicationComponent
			.factory()
			.create(this)
			.inject(this)
	}

	override fun onCreate() {
		super.onCreate()
		val id = FirebaseInstanceId.getInstance().id
		if (BuildConfig.DEBUG) {
			Stetho.initializeWithDefaults(this)
			Timber.plant(Timber.DebugTree())
			Timber.plant(StethoTree())
		}

		WorkManager.initialize(
			this,
			Configuration.Builder()
				.setWorkerFactory(daggerWorkerFactory)
				.build()
		)
	}

}