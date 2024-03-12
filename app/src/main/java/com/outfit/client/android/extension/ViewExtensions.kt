package com.outfit.client.android.extension

import android.view.View

fun Boolean.toVisibleOrGone(): Int = when (this) {
    true -> View.VISIBLE
    false -> View.GONE
}

fun Boolean.toVisibleOrInvisible(): Int = when (this) {
    true -> View.VISIBLE
    false -> View.INVISIBLE
}

fun Boolean.toGoneOrVisible(): Int = when (this) {
    true -> View.GONE
    false -> View.VISIBLE
}

fun Boolean.toInvisibleOrVisible(): Int = when (this) {
    true -> View.INVISIBLE
    false -> View.VISIBLE
}
