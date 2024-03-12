package com.outfit.client.android.ui.authentication.signin

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.navGraphViewModels
import com.outfit.client.android.R
import com.outfit.client.android.data.NetworkState
import com.outfit.client.android.extension.showError
import com.outfit.client.android.pref.SessionPref
import com.outfit.client.android.widget.SyntheticFragment
import kotlinx.android.synthetic.main.fragment_sign_in.*
import javax.inject.Inject

class SignInFragment @Inject constructor(
) : SyntheticFragment(R.layout.fragment_sign_in) {

	private val viewModel: SignInViewModel by navGraphViewModels(R.id.nav_authentication) { viewModelProviderFactory }

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		if (SessionPref.id != 0L
			&& SessionPref.accessToken != null
			&& SessionPref.refreshToken != null
		) {
			SignInFragmentDirections
				.actionSignInFragmentToExploreFragment()
				.navigate()
			return null
		}
		return super.onCreateView(inflater, container, savedInstanceState)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		btn_sign_in.setOnClickListener {
			viewModel.fetchPostPersonToken(
				sign_in_edit_phone_or_email_or_name.text.toString(),
				sign_up_edit_password.text.toString()
			)
		}
		btn_forgot_password.setOnClickListener {
			SignInFragmentDirections.actionSignInFragmentToResetPasswordFragment()
				.navigate()
		}

		btn_navigate_sign_up.setOnClickListener {
			SignInFragmentDirections.actionSignInFragmentToSignUpFragment()
				.navigate()
		}

		viewModel.networkState.observe(viewLifecycleOwner) {
			val state = it.getContentIfNotHandled() ?: return@observe
			when (state) {
				is NetworkState.Fetching -> {
					getSystemService(
						context as Context,
						InputMethodManager::class.java
					)?.hideSoftInputFromWindow(
						activity?.currentFocus?.windowToken,
						InputMethodManager.SHOW_FORCED
					)
					sign_in_layout_phone_or_email_or_name.error = null
					sign_up_layout_password.error = null
					progress.visibility = View.VISIBLE
					form.visibility = View.GONE
				}
				is NetworkState.Success ->
					SignInFragmentDirections.actionSignInFragmentToExploreFragment()
						.navigate()
				is NetworkState.Fail -> {
					progress.visibility = View.GONE
					form.visibility = View.VISIBLE
					showError(state.error) {
						when (it) {
							"email" -> sign_in_layout_phone_or_email_or_name
							"password" -> sign_up_layout_password
							else -> null
						}
					}
				}

			}
		}

		sign_in_edit_phone_or_email_or_name.setText(SessionPref.lastSignInPhoneOrEmailOrName)
	}
}
