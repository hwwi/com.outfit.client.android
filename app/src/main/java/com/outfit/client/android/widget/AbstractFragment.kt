package com.outfit.client.android.widget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.RequestManager
import com.google.android.material.snackbar.Snackbar
import com.outfit.client.android.R
import com.outfit.client.android.data.NetworkState
import com.outfit.client.android.di.DaggerViewModelProviderFactoryBuilder
import com.outfit.client.android.glide.GlideApp
import java.lang.ref.WeakReference
import javax.inject.Inject


abstract class AbstractFragment : Fragment() {

	@Inject
	lateinit var viewModelProviderFactoryBuilder: DaggerViewModelProviderFactoryBuilder

	protected val viewModelProviderFactory: ViewModelProvider.Factory by lazy {
		viewModelProviderFactoryBuilder.buildFactory(this)
	}

	protected val requestManager: RequestManager by lazy { GlideApp.with(this) }


	fun NavDirections.navigate(navOptions: NavOptions? = null) {
		findNavController().navigate(this, navOptions)
	}
}
