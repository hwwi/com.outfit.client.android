package com.outfit.client.android.di

import androidx.navigation.NavBackStackEntry

interface AssistedCurrentBackStackEntryViewModelFactory {
	fun create(currentBackStackEntry: NavBackStackEntry): Any
}
