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
import androidx.navigation.NavAction
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
import com.outfit.client.android.ui.NavActivity
import java.lang.ref.WeakReference
import javax.inject.Inject


abstract class SyntheticFragment(private val layoutRes: Int) : AbstractFragment() {
	@CallSuper
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		val view = inflater.inflate(layoutRes, container, false)
		val toolbar = view.findViewById<Toolbar?>(R.id.toolbar)
		if (toolbar != null) {
			NavigationUI.setupWithNavController(
				toolbar,
				findNavController(),
				NavActivity.createAppBarConfiguration()
			)
		}
		return view
	}
}
