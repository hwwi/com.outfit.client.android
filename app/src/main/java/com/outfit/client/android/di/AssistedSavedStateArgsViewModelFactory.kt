package com.outfit.client.android.di

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavArgs

interface AssistedSavedStateArgsViewModelFactory<Args : NavArgs> {
	fun create(savedState: SavedStateHandle, args: Args): Any
}
