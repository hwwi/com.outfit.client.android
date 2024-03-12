package com.outfit.client.android.livedata

import androidx.annotation.MainThread
import androidx.collection.ArraySet
import androidx.lifecycle.*

class LiveEvent<T> : MediatorLiveData<T>() {
	private val observers = ArraySet<ObserverWrapper<in T>>()

	@MainThread
	override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
		val wrapper = ObserverWrapper(observer)
		observers.add(wrapper)
		super.observe(owner, wrapper)
	}

	@MainThread
	override fun observeForever(observer: Observer<in T>) {
		val wrapper = ObserverWrapper(observer)
		observers.add(wrapper)
		super.observeForever(wrapper)
	}

	@MainThread
	override fun removeObserver(observer: Observer<in T>) {
		if (observers.remove(observer)) {
			super.removeObserver(observer)
			return
		}
		val iterator = observers.iterator()
		while (iterator.hasNext()) {
			val wrapper = iterator.next()
			if (wrapper.observer == observer) {
				iterator.remove()
				super.removeObserver(wrapper)
				break
			}
		}
	}

	@MainThread
	override fun setValue(t: T?) {
		observers.forEach { it.newValue() }
		super.setValue(t)
	}

	internal inner class ObserverWrapper<T>(val observer: Observer<T>) : Observer<T> {

		private var pending = false

		override fun onChanged(t: T?) {
			if (pending) {
				pending = false
				observer.onChanged(t)
			}
		}

		fun newValue() {
			pending = true
		}
	}
}

inline fun <T> LiveData<T>.toLiveEvent(): LiveEvent<T> {
	val liveEvent = LiveEvent<T>()
	liveEvent.addSource(this) {
		liveEvent.value = it
	}
	return liveEvent
}

