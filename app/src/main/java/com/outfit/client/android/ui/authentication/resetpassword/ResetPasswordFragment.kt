package com.outfit.client.android.ui.authentication.resetpassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.outfit.client.android.R
import com.outfit.client.android.data.AnonymousVerificationPurpose
import com.outfit.client.android.data.NetworkState
import com.outfit.client.android.data.PopUpTo
import com.outfit.client.android.databinding.FragmentResetPasswordBinding
import com.outfit.client.android.extension.toast
import com.outfit.client.android.widget.DataBindingFragment
import javax.inject.Inject

class ResetPasswordFragment @Inject constructor(
) : DataBindingFragment<FragmentResetPasswordBinding>(R.layout.fragment_reset_password) {

	private val viewModel: ResetPasswordViewModel by viewModels { viewModelProviderFactory }

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		if (!viewModel.isVerificated) {
			ResetPasswordFragmentDirections
				.actionResetPasswordFragmentToAnonymousVerificationNavigation(
					AnonymousVerificationPurpose.ResetPassword,
					PopUpTo(R.id.resetPasswordFragment, true)
				)
				.navigate()
			return null
		}
		return super.onCreateView(inflater, container, savedInstanceState)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding.networkState = viewModel.networkState
		val menuItemSubmit = binding.mergeToolbar.toolbar.menu.add(R.string.str_submit).apply {
			setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
			setOnMenuItemClickListener {
				viewModel.resetPassword(
					binding.editNewPassword.editableText.toString(),
					binding.editConfirmPassword.editableText.toString()
				)
				true
			}
		}
		viewModel.networkState.observe(viewLifecycleOwner) {
			when (it) {
				is NetworkState.Fetching -> {
					menuItemSubmit.isEnabled = false
				}
				is NetworkState.Success -> {
					menuItemSubmit.isEnabled = true
					toast(R.string.msg_has_been_applied)
					findNavController().popBackStack(R.id.signInFragment, false)
				}
				is NetworkState.Fail -> {
					menuItemSubmit.isEnabled = true
				}
			}
		}
	}

}