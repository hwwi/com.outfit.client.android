package com.outfit.client.android.di.module

import com.outfit.client.android.di.ChildWorkerFactory
import com.outfit.client.android.di.key.WorkerKey
import com.outfit.client.android.work.UploadPushTokenWorker
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface WorkerBindModule {

	@Binds
	@IntoMap
	@WorkerKey(UploadPushTokenWorker::class)
	fun bindUploadPushTokenWorker(factory: UploadPushTokenWorker.Factory): ChildWorkerFactory
}