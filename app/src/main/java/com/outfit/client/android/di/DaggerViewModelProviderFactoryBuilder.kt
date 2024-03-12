package com.outfit.client.android.di

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.collection.ArrayMap
import androidx.fragment.app.Fragment
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavArgs
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.savedstate.SavedStateRegistryOwner
import com.outfit.client.android.extension.getOrFindAssignable
import com.outfit.client.android.ui.NavActivity
import java.lang.reflect.Method
import javax.inject.Inject
import javax.inject.Provider


internal val methodSignature = arrayOf(Bundle::class.java)
internal val methodMap = ArrayMap<Class<out NavArgs>, Method>()

@Suppress("UNCHECKED_CAST")
class DaggerViewModelProviderFactoryBuilder @Inject constructor(
	private val assistedArgsViewModelFactories: Map<Class<out ViewModel>, @JvmSuppressWildcards AssistedArgsViewModelFactory<*>>,
	private val assistedSavedStateViewModelFactories: Map<Class<out ViewModel>, @JvmSuppressWildcards AssistedSavedStateViewModelFactory>,
	private val assistedSavedStateArgsViewModelFactories: Map<Class<out ViewModel>, @JvmSuppressWildcards AssistedSavedStateArgsViewModelFactory<*>>,

	private val assistedCurrentBackStackEntryViewModelFactories: Map<Class<out ViewModel>, @JvmSuppressWildcards AssistedCurrentBackStackEntryViewModelFactory>,
	private val assistedPreviousBackStackEntryViewModelFactories: Map<Class<out ViewModel>, @JvmSuppressWildcards AssistedPreviousBackStackEntryViewModelFactory>,

	private val viewModelProviders: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
) {

	fun buildFactory(activity: AppCompatActivity): ViewModelProvider.Factory =
		buildFactory(activity, activity.intent.extras, null)

	fun buildFactory(fragment: Fragment): ViewModelProvider.Factory =
		buildFactory(fragment, fragment.requireActivity().intent.extras, fragment.arguments)

	private fun buildFactory(
		owner: SavedStateRegistryOwner,
		defaultArgs: Bundle?,
		argument: Bundle?
	): ViewModelProvider.Factory = DaggerSavedStateViewModelFactory(owner, defaultArgs, argument)

	private inner class DaggerSavedStateViewModelFactory(
		private val owner: SavedStateRegistryOwner,
		defaultArgs: Bundle?,
		private val argument: Bundle?
	) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
		override fun <T : ViewModel> create(
			key: String,
			modelClass: Class<T>,
			handle: SavedStateHandle
		): T {
			val viewModel = assistedSavedStateViewModelFactories.getOrFindAssignable(modelClass)
				?.create(handle)
				?: assistedArgsViewModelFactories.getOrFindAssignable(modelClass)
					?.run {
						this as? AssistedArgsViewModelFactory<NavArgs>
							?: throw IllegalArgumentException("Generic type is not NavArgs")
						create(createNavArgs(modelClass, argument))
					}
				?: assistedSavedStateArgsViewModelFactories.getOrFindAssignable(modelClass)
					?.run {
						this as? AssistedSavedStateArgsViewModelFactory<NavArgs>
							?: throw IllegalArgumentException("Generic type is not NavArgs")
						create(handle, createNavArgs(modelClass, argument))
					}
				?: assistedCurrentBackStackEntryViewModelFactories.getOrFindAssignable(modelClass)
					?.create(
						findNavController(owner).currentBackStackEntry
							?: throw IllegalStateException("currentBackStackEntry is null")
					)
				?: assistedPreviousBackStackEntryViewModelFactories.getOrFindAssignable(modelClass)
					?.create(
						findNavController(owner).previousBackStackEntry
							?: throw IllegalStateException("previousBackStackEntry is null")
					)
				?: viewModelProviders.getOrFindAssignable(modelClass)?.get()
				?: throw IllegalArgumentException("unknown model class $modelClass")
			return viewModel as T
		}

		private fun findNavController(owner: SavedStateRegistryOwner): NavController =
			when (owner) {
				is NavActivity -> owner.findNavController()
				is Fragment -> owner.findNavController()
				else -> throw IllegalStateException("Cant find NavController")
			}

		private fun <T : ViewModel, Args : NavArgs> createNavArgs(
			modelClass: Class<T>,
			arguments: Bundle?
		): Args {
			if (modelClass.constructors.size != 1)
				throw Error("constructor must be one")

			val constructor = modelClass.constructors.first()
			val navArgsClass = constructor.parameterTypes.firstOrNull { parameterType ->
				NavArgs::class.java.isAssignableFrom(parameterType)
			} as? Class<Args> ?: throw Error("NavArgs.class Assignable type is not exist")

			val method: Method = methodMap[navArgsClass]
				?: navArgsClass.getMethod("fromBundle", *methodSignature).also { method ->
					methodMap[navArgsClass] = method
				}

			val invoke = method.invoke(null, arguments)
			return invoke as Args
		}
	}
}