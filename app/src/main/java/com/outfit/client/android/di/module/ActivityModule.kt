package com.outfit.client.android.di.module

import dagger.Module


@Module(
	includes = [
		ActivityModule.ProvidesModule::class,
		FragmentBindModule::class,
		ViewModelBindModule::class,
		FragmentModule::class
	]
)
interface ActivityModule {

	@Module
	class ProvidesModule
}