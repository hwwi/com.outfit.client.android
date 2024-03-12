package com.outfit.client.android.widget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.google.android.material.snackbar.Snackbar
import com.outfit.client.android.R
import com.outfit.client.android.data.NetworkState
import com.outfit.client.android.di.DaggerViewModelProviderFactoryBuilder
import com.outfit.client.android.glide.GlideApp
import java.lang.ref.WeakReference
import javax.inject.Inject

abstract class AbstractDialog(
	private val layoutRes: Int
) : DialogFragment() {

	@Inject
	lateinit var viewModelProviderFactoryBuilder: DaggerViewModelProviderFactoryBuilder

	protected val viewModelProviderFactory: ViewModelProvider.Factory
		get() = viewModelProviderFactoryBuilder.buildFactory(this)


	protected val requestManager: RequestManager by lazy { GlideApp.with(this) }
	private var resourceStateProgress: WeakReference<ProgressBar>? = null

	final override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		val view = inflater.inflate(layoutRes, container, false)
		resourceStateProgress = view.findViewById<ProgressBar>(R.id.progress)
			?.let { WeakReference(it) }
		return view
	}

	fun observeNetworkState(
		networkStateLiveData: LiveData<NetworkState<Unit>>,
		retryCallback: (() -> Unit)? = null
	) {
		networkStateLiveData.observe(viewLifecycleOwner) {
			resourceStateProgress?.get()?.visibility = when (it) {
				is NetworkState.Fetching -> View.VISIBLE
				else -> View.INVISIBLE
			}
			if (it is NetworkState.Fail) {
				Snackbar
					.make(requireView(), it.error.message ?: "network fail", Snackbar.LENGTH_LONG)
					.apply {
						if (retryCallback != null)
							setAction("Retry") { retryCallback() }
					}
					.show()
			}
		}
	}



	fun NavDirections.navigate(navOptions: NavOptions? = null) {
		findNavController().navigate(this, navOptions)
	}
}