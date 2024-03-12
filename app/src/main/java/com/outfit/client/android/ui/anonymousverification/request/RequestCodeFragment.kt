package com.outfit.client.android.ui.anonymousverification.request

import android.os.Bundle
import android.text.InputType
import android.view.View
import androidx.activity.addCallback
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.google.android.material.tabs.TabLayout
import com.outfit.client.android.R
import com.outfit.client.android.data.NetworkState
import com.outfit.client.android.data.payload.PostRequestAnonymousVerificationPayload
import com.outfit.client.android.extension.selectedTab
import com.outfit.client.android.extension.showError
import com.outfit.client.android.ui.anonymousverification.verify.VerifyCodeViewModel
import com.outfit.client.android.widget.SyntheticFragment
import com.outfit.client.android.widget.adapter.PhoneNumberSupportedRegionAdapter
import kotlinx.android.synthetic.main.fragment_anonymous_verification_request.*
import java.util.*
import javax.inject.Inject

class RequestCodeFragment @Inject constructor(

) : SyntheticFragment(R.layout.fragment_anonymous_verification_request) {

	private val args: RequestCodeFragmentArgs by navArgs()
	private val viewModel: RequestCodeViewModel by viewModels { viewModelProviderFactory }

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		val previousBackStackEntry = findNavController().previousBackStackEntry
			?: throw IllegalStateException()
		previousBackStackEntry.savedStateHandle
			.remove<PostRequestAnonymousVerificationPayload>(VerifyCodeViewModel.KEY_VERIFICATION_PAYLOAD)
		val requestDestinationId: Int = previousBackStackEntry.destination.id

		requireActivity().onBackPressedDispatcher.addCallback(this) {
			when (val popUpTo = args.popUpTo) {
				null -> findNavController().popBackStack()
				else -> findNavController().popBackStack(
					popUpTo.destinationId,
					popUpTo.inclusive
				)
			}
		}

		val adapter = PhoneNumberSupportedRegionAdapter()
		sign_up_verification_spinner_region.adapter = adapter
		val defaultLocale = Locale.getDefault()
		val position = adapter.items.indexOfFirst {
			it.region == defaultLocale.country
		}
		if (position != -1)
			sign_up_verification_spinner_region.setSelection(position)

		sign_up_verification_edit_to.addTextChangedListener {
			sign_up_verification_button_next.isEnabled = it?.isNotBlank() == true
		}

		sign_up_verification_button_next.setOnClickListener {
			val to = sign_up_verification_edit_to.text?.toString()
				?: return@setOnClickListener

			when (sign_up_verification_tabLayout.selectedTab?.tag) {
				R.id.sign_up_verification_tab_sms -> {
					val supportedRegion =
						adapter.getItem(sign_up_verification_spinner_region.selectedItemPosition)
					viewModel.requestVerification(to, supportedRegion.region)
				}
				R.id.sign_up_verification_tab_email -> viewModel.requestVerification(to)
			}
		}

		// layout.xml 에서 {@link TabItem} 의 속성이 tag 누락되어있음..
		sign_up_verification_tabLayout.getTabAt(0)?.tag = R.id.sign_up_verification_tab_sms
		sign_up_verification_tabLayout.getTabAt(1)?.tag = R.id.sign_up_verification_tab_email

		sign_up_verification_tabLayout.addOnTabSelectedListener(
			object : TabLayout.OnTabSelectedListener {
				override fun onTabReselected(tab: TabLayout.Tab) {}

				override fun onTabUnselected(tab: TabLayout.Tab) {}

				override fun onTabSelected(tab: TabLayout.Tab) {
					when (tab.tag) {
						R.id.sign_up_verification_tab_sms -> {
							sign_up_verification_edit_to.inputType = InputType.TYPE_CLASS_PHONE
							sign_up_verification_spinner_region.visibility = View.VISIBLE
							sign_up_verification_layout_to.hint = getString(R.string.str_phone)
						}
						R.id.sign_up_verification_tab_email -> {
							sign_up_verification_edit_to.inputType =
								InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
							sign_up_verification_spinner_region.visibility = View.GONE
							sign_up_verification_layout_to.hint = getString(R.string.str_email)
						}
					}
					sign_up_verification_edit_to.text = null
				}
			}
		)

		viewModel.networkState.observe(viewLifecycleOwner) {
			when (it) {
				is NetworkState.Fetching -> {
					val drawable = CircularProgressDrawable(requireContext()).apply {
						setStyle(CircularProgressDrawable.DEFAULT)
					}
					sign_up_verification_button_next.icon = drawable
					drawable.start()
					sign_up_verification_button_next.isEnabled = false
					sign_up_verification_layout_to.error = null
					sign_up_verification_layout_to.isEnabled = false
				}
				is NetworkState.Success -> {
					sign_up_verification_button_next.icon = null
					sign_up_verification_button_next.isEnabled = true
					sign_up_verification_layout_to.isEnabled = true

					RequestCodeFragmentDirections
						.actionRequestCodeFragmentToVerifyCodeFragment(
							requestDestinationId,
							it.data
						)
						.navigate()
				}
				is NetworkState.Fail -> {
					sign_up_verification_button_next.icon = null
					sign_up_verification_button_next.isEnabled = true
					sign_up_verification_layout_to.isEnabled = true
					showError(it.error) { field ->
						when (field) {
							"number",
							"email " -> sign_up_verification_layout_to
							else -> null
						}
					}
				}
			}
		}
	}
}