package com.outfit.client.android.widget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.outfit.client.android.R
import com.outfit.client.android.ui.NavActivity
import com.outfit.client.android.util.autoCleared


abstract class DataBindingFragment<T : ViewDataBinding>(
	private val layoutRes: Int
) : AbstractFragment() {

	protected var binding by autoCleared<T>()

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		binding = DataBindingUtil.inflate(layoutInflater, layoutRes, container, false)
		val toolbar = binding.root.findViewById<Toolbar?>(R.id.toolbar)
		if (toolbar != null) {
			NavigationUI.setupWithNavController(
				toolbar,
				findNavController(),
				NavActivity.createAppBarConfiguration()
			)
		}
		return binding.root
	}

	@CallSuper
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		binding.lifecycleOwner = viewLifecycleOwner
	}
}
