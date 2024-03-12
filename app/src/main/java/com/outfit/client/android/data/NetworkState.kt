package com.outfit.client.android.data

@Suppress("DataClassPrivateConstructor")
sealed class NetworkState<out T> {
	companion object {
		fun <T> fetching(data: T? = null): NetworkState<T> = Fetching.create(data)
		fun <T> success(data: T): NetworkState<T> = Success.create(data)
		fun <T> fail(error: Throwable): NetworkState<T> = Fail.create(error)
	}

	data class Fetching<out T> private constructor(
		val data: T?
	) : NetworkState<T>() {
		companion object {
			fun <T> create(data: T? = null): NetworkState<T> = Fetching(data)
		}
	}

	data class Success<out T> private constructor(
		val data: T
	) : NetworkState<T>() {
		companion object {
			fun <T> create(data: T): NetworkState<T> = Success(data)
		}
	}

	data class Fail<out T> private constructor(
		val error: Throwable
	) : NetworkState<T>() {
		companion object {
			fun <T> create(error: Throwable): NetworkState<T> = Fail(error)
		}
	}

	override fun toString(): String {
		return javaClass.simpleName
	}
}