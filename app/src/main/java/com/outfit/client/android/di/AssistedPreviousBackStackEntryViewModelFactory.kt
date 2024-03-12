package com.outfit.client.android.di

import androidx.navigation.NavBackStackEntry

interface AssistedPreviousBackStackEntryViewModelFactory {
	fun create(previousBackStackEntry: NavBackStackEntry): Any
}
