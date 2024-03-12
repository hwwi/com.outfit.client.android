package com.outfit.client.android.ui.changename

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.outfit.client.android.R
import com.outfit.client.android.data.NetworkState
import com.outfit.client.android.databinding.FragmentChangeNameBinding
import com.outfit.client.android.extension.showError
import com.outfit.client.android.extension.toast
import com.outfit.client.android.widget.DataBindingFragment
import javax.inject.Inject

class ChangeNameFragment @Inject constructor(
) : DataBindingFragment<FragmentChangeNameBinding>(R.layout.fragment_change_name) {
	private val viewModel: ChangeNameViewModel by viewModels { viewModelProviderFactory }

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding.networkState = viewModel.submitState
		val menuItemSubmit = binding.mergeToolbar.toolbar.menu.add(R.string.str_submit).apply {
			setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
			setOnMenuItemClickListener {
				val name = binding.editName.text?.toString()
				if (!name.isNullOrBlank())
					viewModel.submitChanges(name)
				true
			}
		}

		binding.editName.addTextChangedListener {
			val name = it?.toString() ?: ""
			binding.editName.isEnabled = name.isNotBlank()
			viewModel.validateName(name)
		}

		viewModel.validateNameState.observe(viewLifecycleOwner) { networkState ->
			when (networkState) {
				is NetworkState.Fetching -> {
					val drawable = CircularProgressDrawable(requireContext()).apply {
						setStyle(CircularProgressDrawable.DEFAULT)
					}
					binding.layoutName.endIconDrawable = drawable
					drawable.start()
					binding.layoutName.error = null
				}
				is NetworkState.Success -> {
					binding.layoutName.endIconDrawable = null
				}
				is NetworkState.Fail -> {
					binding.layoutName.endIconDrawable = null
					showError(networkState.error) {
						when (it) {
							"name" -> binding.layoutName
							else -> null
						}
					}
				}
			}
		}

		viewModel.submitState.observe(viewLifecycleOwner) {
			when (it) {
				is NetworkState.Fetching -> {
					menuItemSubmit.isEnabled = false
				}
				is NetworkState.Success -> {
					menuItemSubmit.isEnabled = true
					toast(R.string.msg_has_been_applied)
				}
				is NetworkState.Fail -> {
					menuItemSubmit.isEnabled = true
				}
			}
		}
	}

}