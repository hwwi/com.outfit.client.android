package com.outfit.client.android.ui.changepassword

import android.os.Bundle
import android.text.Editable
import android.view.MenuItem
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import com.outfit.client.android.R
import com.outfit.client.android.data.NetworkState
import com.outfit.client.android.databinding.FragmentChangePasswordBinding
import com.outfit.client.android.extension.toast
import com.outfit.client.android.widget.DataBindingFragment
import javax.inject.Inject

class ChangePasswordFragment @Inject constructor(
) : DataBindingFragment<FragmentChangePasswordBinding>(R.layout.fragment_change_password) {
	private val viewModel: ChangePasswordViewModel by viewModels { viewModelProviderFactory }

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding.networkState = viewModel.submitState

		val menuItemSubmit = binding.mergeToolbar.toolbar.menu.add(R.string.str_submit).apply {
			setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
			setOnMenuItemClickListener {
				val currentPassword = binding.editCurrentPassword.text?.toString()
				val newPassword = binding.editNewPassword.text?.toString()
				val confirmPassword = binding.editConfirmPassword.text?.toString()
				if (
					!currentPassword.isNullOrBlank()
					&& !newPassword.isNullOrBlank()
					&& !confirmPassword.isNullOrBlank()
				)
					viewModel.submitChanges(
						currentPassword,
						newPassword,
						confirmPassword
					)
				true
			}
		}

		fun toggleSubmitEnable(
			currentPassword: Editable? = binding.editCurrentPassword.text,
			newPassword: Editable? = binding.editNewPassword.text,
			confirmPassword: Editable? = binding.editConfirmPassword.text
		) {
			menuItemSubmit.isEnabled =
				!currentPassword.isNullOrBlank()
					&& !newPassword.isNullOrBlank()
					&& !confirmPassword.isNullOrBlank()
		}
		binding.editCurrentPassword.addTextChangedListener {
			toggleSubmitEnable(currentPassword = it)
		}
		binding.editNewPassword.addTextChangedListener {
			toggleSubmitEnable(newPassword = it)
		}
		binding.editConfirmPassword.addTextChangedListener {
			toggleSubmitEnable(confirmPassword = it)
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