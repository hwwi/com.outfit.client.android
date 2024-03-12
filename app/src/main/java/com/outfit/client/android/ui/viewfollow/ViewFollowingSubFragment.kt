package com.outfit.client.android.ui.viewfollow

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.navGraphViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.outfit.client.android.R
import com.outfit.client.android.extension.showError
import com.outfit.client.android.pref.SessionPref
import com.outfit.client.android.widget.SyntheticFragment
import com.outfit.client.android.widget.controller.FollowingController
import kotlinx.android.synthetic.main.sub_fragment_view_follow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class ViewFollowingSubFragment @Inject constructor(
) : SyntheticFragment(R.layout.sub_fragment_view_follow) {

	private val viewModel: ViewFollowingSubViewModel by navGraphViewModels(R.id.viewFollowFragment) { viewModelProviderFactory }
	private val personId: Long by lazy {
		arguments?.getLong(ViewFollowFragment.KEY_PERSON_ID) ?: throw IllegalArgumentException()
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		sub_view_follow_edit_query.addTextChangedListener {
			requestFetchFollowingsByKeyword(it?.toString())
		}

		val controller = FollowingController(
			isUnfollowButtonVisible = SessionPref.id == personId,
			onPersonClick = { person ->
				ViewFollowFragmentDirections
					.actionToClosetFragment(null, person.id)
					.navigate()
			},
			onUnfollowButtonClickListener = { person ->
				MaterialAlertDialogBuilder(requireContext())
					.setTitle(R.string.dialog_title_delete_following)
					.setMessage(R.string.dialog_content_delete_following)
					.setPositiveButton(R.string.str_ok) { _, _ ->
						viewLifecycleOwner.lifecycleScope.launch {
							viewModel.deleteFollowing(person.id) {
								showError(it)
							}
						}
					}
					.setNegativeButton(R.string.str_cancel, null)
					.show()
			}
		)
		sub_view_follow_list_person.setController(controller)
		viewModel.followings.observe(viewLifecycleOwner) {
			controller.submitList(it)
		}
		requestFetchFollowingsByKeyword(null)
	}

	private fun requestFetchFollowingsByKeyword(keyword: String?) {
		viewModel.requestFetchFollowingsByKeyword(personId, keyword)
	}
}