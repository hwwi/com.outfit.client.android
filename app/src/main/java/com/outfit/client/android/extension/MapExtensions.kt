package com.outfit.client.android.extension

fun <K, V> Map<Class<out K>, V>.getOrFindAssignable(cls: Class<out K>): V? =
	get(cls) ?: asIterable().firstOrNull { cls.isAssignableFrom(it.key) }?.value