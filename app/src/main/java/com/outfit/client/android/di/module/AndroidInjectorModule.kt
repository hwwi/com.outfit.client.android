package com.outfit.client.android.di.module

import com.outfit.client.android.di.scope.ActivityScope
import com.outfit.client.android.service.OutfitFirebaseMessagingService
import com.outfit.client.android.ui.NavActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
interface AndroidInjectorModule {

	@ActivityScope
	@ContributesAndroidInjector(modules = [ActivityModule::class])
	fun contributeNavActivity(): NavActivity

	@ActivityScope
	@ContributesAndroidInjector
	fun contributeOutfitFirebaseMessagingService(): OutfitFirebaseMessagingService

}