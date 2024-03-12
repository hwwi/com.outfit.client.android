package com.outfit.client.android.ui.myaccount

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.outfit.client.android.R
import com.outfit.client.android.extension.showError
import com.outfit.client.android.pref.SessionPref
import com.outfit.client.android.ui.NavActivity
import com.outfit.client.android.widget.SyntheticFragment
import kotlinx.android.synthetic.main.fragment_my_account.*
import kotlinx.coroutines.launch
import javax.inject.Inject


class MyAccountFragment @Inject constructor(
) : SyntheticFragment(R.layout.fragment_my_account) {
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		my_account_button_my_closet.setOnClickListener {
			MyAccountFragmentDirections
				.actionMyAccountFragmentToClosetFragment(null, SessionPref.id)
				.navigate()
		}
		my_account_button_bookmarks.setOnClickListener{
			MyAccountFragmentDirections
				.actionMyAccountFragmentToBookmarksFragment()
				.navigate()
		}
		my_account_button_edit_profile.setOnClickListener {
			MyAccountFragmentDirections
				.actionMyAccountFragmentToEditProfileFragment()
				.navigate()
		}
		my_account_button_change_password.setOnClickListener {
			MyAccountFragmentDirections
				.actionMyAccountFragmentToChangePasswordFragment()
				.navigate()
		}
		my_account_button_logout.setOnClickListener {
			viewLifecycleOwner.lifecycleScope.launch {
				it.isEnabled = false
				val activity = requireActivity() as NavActivity
				try {
					activity.logout()
				} catch (e: Exception) {
					showError(e)
				}
				it.isEnabled = true
			}
		}
	}
}