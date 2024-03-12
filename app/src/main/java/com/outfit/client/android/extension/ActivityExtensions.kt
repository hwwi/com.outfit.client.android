package com.outfit.client.android.extension

import android.app.Activity
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat

fun Activity.hideSoftInputFromWindow(){
	ContextCompat.getSystemService(
		this,
		InputMethodManager::class.java
	)
		?.hideSoftInputFromWindow(
		currentFocus?.windowToken,
			InputMethodManager.SHOW_FORCED
	)
}