package com.outfit.client.android.di.module

import dagger.Module

@Module(
	includes = [
		FragmentModule.ProvidesModule::class
	]
)
interface FragmentModule {

	@Module
	class ProvidesModule
}