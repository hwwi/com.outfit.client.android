package com.outfit.client.android.util


import android.os.SystemClock
import androidx.collection.ArrayMap

import java.util.concurrent.TimeUnit

/**
 * Utility class that decides whether we should fetch some data or not.
 */
class RateLimiter<in KEY>(timeout: Int, timeUnit: TimeUnit) {
	private val timestamps = ArrayMap<KEY, Long>()
	private val timeout = timeUnit.toMillis(timeout.toLong())

	@Synchronized
	fun shouldFetch(key: KEY): Boolean {
		val lastFetched = timestamps[key]
		val now = SystemClock.uptimeMillis()
		if (lastFetched == null) {
			timestamps[key] = now
			return true
		}
		if (now - lastFetched > timeout) {
			timestamps[key] = now
			return true
		}
		return false
	}

	@Synchronized
	fun reset(key: KEY) {
		timestamps.remove(key)
	}
}
