package com.outfit.client.android.util

import android.database.Cursor
import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

@MainThread
inline fun <T> LiveData<T>.observeOnce(
	owner: LifecycleOwner,
	crossinline onChanged: (T) -> Unit
): Observer<T> {
	val wrappedObserver = object : Observer<T> {
		override fun onChanged(t: T) {
			onChanged.invoke(t)
			removeObserver(this)
		}
	}
	observe(owner, wrappedObserver)
	return wrappedObserver
}

fun Cursor.getInt(name: String): Int = getInt(getColumnIndex(name))
fun Cursor.getLong(name: String): Long = getLong(getColumnIndex(name))
fun Cursor.getString(name: String): String = getString(getColumnIndex(name))
fun Cursor.asSequence(): Sequence<Cursor> =
	generateSequence(seed = takeIf { it.moveToFirst() }) { takeIf { it.moveToNext() } }

fun Cursor.toIterable() = IterableCursor(this)


fun <R, D : MutableCollection<in R>> Cursor?.useSequenceTo(
	destination: D,
	transform: (Cursor) -> R
): D = this?.use { c ->
	generateSequence(seed = takeIf { c.moveToFirst() }) { takeIf { c.moveToNext() } }
		.mapTo(destination, transform)
} ?: destination

fun <R> Cursor?.useSequence(transform: (Cursor) -> R): List<R> = useSequenceTo(arrayListOf(), transform)

//커서의 첫번째 줄만 읽어서 transform 시킨 결과를 리턴, 커서가 비어있으면 null
fun <R> Cursor?.useFirst(transform: (Cursor) -> R): R? = this?.use { c ->
	when {
		c.moveToFirst() -> transform(c)
		else -> null
	}
}