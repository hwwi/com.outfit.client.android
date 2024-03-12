package com.outfit.client.android.di

import androidx.lifecycle.SavedStateHandle

interface AssistedSavedStateViewModelFactory {
	fun create(savedState: SavedStateHandle): Any
}
