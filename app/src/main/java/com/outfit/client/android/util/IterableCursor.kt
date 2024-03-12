package com.outfit.client.android.util

import android.database.Cursor

class IterableCursor(private val mIterableCursor: Cursor) : Iterable<Cursor> {

	override fun iterator(): Iterator<Cursor> {
		return IteratorCursor(mIterableCursor)
	}

	internal class IteratorCursor(private val mCursor: Cursor) : Iterator<Cursor> {

		override fun hasNext(): Boolean {
			return !mCursor.isClosed && mCursor.moveToNext()
		}

		override fun next(): Cursor {
			return mCursor
		}

	}
}