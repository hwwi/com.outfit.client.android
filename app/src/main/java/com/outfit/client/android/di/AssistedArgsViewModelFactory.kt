package com.outfit.client.android.di

import androidx.navigation.NavArgs

interface AssistedArgsViewModelFactory<Args : NavArgs> {
	fun create(args: Args): Any
}
