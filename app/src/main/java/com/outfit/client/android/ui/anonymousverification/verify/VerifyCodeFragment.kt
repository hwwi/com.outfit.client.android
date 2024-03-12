package com.outfit.client.android.ui.anonymousverification.verify

import android.os.Bundle
import android.text.InputFilter
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.outfit.client.android.R
import com.outfit.client.android.data.NetworkState
import com.outfit.client.android.extension.showError
import com.outfit.client.android.widget.SyntheticFragment
import kotlinx.android.synthetic.main.fragment_anonymous_verification_verify.*
import javax.inject.Inject

class VerifyCodeFragment @Inject constructor(

) : SyntheticFragment(R.layout.fragment_anonymous_verification_verify) {

	val viewModel: VerifyCodeViewModel by viewModels { viewModelProviderFactory }
	val args: VerifyCodeFragmentArgs by navArgs()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		anonymous_verification_verify_text_head.text =
			getString(
				R.string.msg_enter_the_verification_code_sent_to,
				args.requestVerificationPayload.to,
				args.requestVerificationPayload.codeLength
			)
		anonymous_verification_verify_button_request_new_code.setOnClickListener {
			viewModel.requestNewCode()
		}
		anonymous_verification_verify_edit_code.filters += InputFilter.AllCaps()
		anonymous_verification_verify_edit_code.addTextChangedListener {
			anonymous_verification_verify_button_next.isEnabled =
				it?.length == args.requestVerificationPayload.codeLength
		}

		anonymous_verification_verify_button_next.setOnClickListener {
			val code = anonymous_verification_verify_edit_code.text?.toString()
				?: return@setOnClickListener
			viewModel.verifyCode(code)
		}

		viewModel.networkState.observe(viewLifecycleOwner) { state ->
			when (state) {
				is NetworkState.Fetching -> {
					val drawable = CircularProgressDrawable(requireContext()).apply {
						setStyle(CircularProgressDrawable.DEFAULT)
					}
					anonymous_verification_verify_button_next.icon = drawable
					drawable.start()
					anonymous_verification_verify_button_next.isEnabled = false
					anonymous_verification_verify_edit_code.error = null
					anonymous_verification_verify_edit_code.isEnabled = false
				}
				is NetworkState.Success -> {
					anonymous_verification_verify_button_next.icon = null
					anonymous_verification_verify_button_next.isEnabled = true
					anonymous_verification_verify_edit_code.isEnabled = true
				}
				is NetworkState.Fail -> {
					anonymous_verification_verify_button_next.icon = null
					anonymous_verification_verify_button_next.isEnabled = true
					anonymous_verification_verify_edit_code.isEnabled = true

					when (state.error) {
						else -> showError(state.error) {
							when (it) {
//								"name" -> edit_profile_edit_person_name
								else -> null
							}
						}
					}
				}
			}
		}
		viewModel.onVerifySuccess.observe(viewLifecycleOwner) {
			findNavController().apply {
				getBackStackEntry(args.requestDestinationId).savedStateHandle.set(
					VerifyCodeViewModel.KEY_VERIFICATION_PAYLOAD,
					it
				)
				popBackStack(R.id.anonymous_verification_navigation, true)
			}
		}
	}
}