package com.outfit.client.android.di.component

import android.content.Context
import com.outfit.client.android.OutfitApp
import com.outfit.client.android.di.module.*
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Component(
	modules = [
		AndroidInjectionModule::class,
		AssistedInjectModule::class,
		ApplicationModule::class,
		AndroidInjectorModule::class,
		JsonModule::class,
		NetworkModule::class,
		DatabaseModule::class,
		WorkerBindModule::class
	]
)
//@ApplicationScope
@Singleton
interface ApplicationComponent : AndroidInjector<OutfitApp> {

	@Component.Factory
	interface Factory {

		fun create(@BindsInstance context: Context): ApplicationComponent

	}
}