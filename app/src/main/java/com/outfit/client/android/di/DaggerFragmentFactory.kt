package com.outfit.client.android.di

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.outfit.client.android.extension.getOrFindAssignable
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Provider

class DaggerFragmentFactory @Inject constructor(
	private val providers: Map<Class<out Fragment>, @JvmSuppressWildcards Provider<Fragment>>
) : FragmentFactory() {

	override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
		val fragmentClass = loadFragmentClass(classLoader, className)
		return when (val fragment = instantiate(fragmentClass)) {
			null -> {
				Timber.w("No provider found for class: $className. Using default constructor")
				super.instantiate(classLoader, className)
			}
			else -> fragment
		}
	}

	fun instantiate(fragmentClass: Class<out Fragment>): Fragment? =
		when (val provider = providers.getOrFindAssignable(fragmentClass)) {
			null -> null
			else -> provider.get()
		}

}