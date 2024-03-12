package com.outfit.client.android.di.key

import androidx.work.ListenableWorker
import dagger.MapKey
import kotlin.reflect.KClass

/*
 * WorkerKey (di)
 * @desc MapKey annotation class WorkerKey
 * hiup/speed (di)
 */
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class WorkerKey(val value: KClass<out ListenableWorker>)
