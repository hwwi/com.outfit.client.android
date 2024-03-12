package com.outfit.client.android.ui.authentication.signup

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.outfit.client.android.R
import com.outfit.client.android.data.AnonymousVerificationPurpose
import com.outfit.client.android.data.NetworkState
import com.outfit.client.android.data.PopUpTo
import com.outfit.client.android.data.VerificationMethod
import com.outfit.client.android.extension.hideSoftInputFromWindow
import com.outfit.client.android.extension.showError
import com.outfit.client.android.extension.toast
import com.outfit.client.android.widget.SyntheticFragment
import kotlinx.android.synthetic.main.fragment_sign_up.*
import javax.inject.Inject

class SignUpFragment @Inject constructor(
) : SyntheticFragment(R.layout.fragment_sign_up) {

	private val viewModel: SignUpViewModel by viewModels { viewModelProviderFactory }

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		if (!viewModel.isVerificated) {
			SignUpFragmentDirections
				.actionSignUpFragmentToAnonymousVerificationNavigation(
					AnonymousVerificationPurpose.SignUp,
					PopUpTo(R.id.signUpFragment, true)
				)
				.navigate()
			return null
		}
		return super.onCreateView(inflater, container, savedInstanceState)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		sign_up_et_name.addTextChangedListener {
			viewModel.validateName(it?.toString() ?: "")
		}
		sign_up_button_sign_up.setOnClickListener {
			viewModel.fetchPostPerson(
				sign_up_et_name.text.toString(),
				sign_up_edit_password.text.toString(),
				sign_up_edit_confirm_password.text.toString()
			)
		}

		sign_up_button_sign_in.setOnClickListener {
			findNavController().popBackStack()
		}

		viewModel.verificationPayload.observe(viewLifecycleOwner) {
			sign_up_et_to.setText(it.to)
			when (it.method) {
				VerificationMethod.Sms -> {
					sign_up_et_to.inputType = InputType.TYPE_CLASS_PHONE
					sign_up_layout_to.hint = getString(R.string.str_phone)
				}
				VerificationMethod.Email -> {
					sign_up_et_to.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
					sign_up_layout_to.hint = getString(R.string.str_email)
				}
			}
		}

		viewModel.validateNameState.observe(viewLifecycleOwner) { state ->
			when (state) {
				is NetworkState.Fetching -> {
					val drawable = CircularProgressDrawable(requireContext()).apply {
						setStyle(CircularProgressDrawable.DEFAULT)
					}
					sign_up_layout_name.endIconDrawable = drawable
					drawable.start()
					sign_up_layout_name.error = null
				}
				is NetworkState.Success -> {
					sign_up_layout_name.endIconDrawable = null
				}
				is NetworkState.Fail -> {
					sign_up_layout_name.endIconDrawable = null
					showError(state.error) {
						when (it) {
							"name" -> sign_up_layout_name
							else -> null
						}
					}
				}
			}
		}
		viewModel.networkState.observe(viewLifecycleOwner) { state ->
			when (state) {
				is NetworkState.Fetching -> {
					activity?.hideSoftInputFromWindow()
					sign_up_layout_to.error = null
					sign_up_layout_name.error = null
					sign_up_layout_password.error = null
					sign_up_layout_confirm_password.error = null
					progress.visibility = View.VISIBLE
					form.visibility = View.GONE
				}
				is NetworkState.Success -> {
					toast(R.string.msg_sign_up_complete)
					findNavController().popBackStack()
				}
				is NetworkState.Fail -> {
					progress.visibility = View.GONE
					form.visibility = View.VISIBLE
					showError(state.error) {
						when (it) {
							"email" -> sign_up_layout_to
							"name" -> sign_up_layout_name
							"password" -> sign_up_layout_password
							"confirmPassword" -> sign_up_layout_confirm_password
							else -> null
						}
					}
				}

			}
		}
	}
}
