package com.outfit.client.android.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.atomic.AtomicBoolean

open class Event<out T>(
	private val content: T
) {
	val hasBeenHandled = AtomicBoolean(false)

	fun getContentIfNotHandled(): T? {
		if (hasBeenHandled.compareAndSet(false, true))
			return content
		return null
	}

	fun peekContent() = content
}

fun <T> LiveData<T>.toEvent(): LiveData<Event<T>> = map { Event(it) }

fun <T> Flow<T>.toEvent(): Flow<Event<T>> = map { Event(it) }