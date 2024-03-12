package com.outfit.client.android.ui.viewfollow

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.navigation.navGraphViewModels
import com.outfit.client.android.R
import com.outfit.client.android.widget.SyntheticFragment
import com.outfit.client.android.widget.controller.FollowerController
import kotlinx.android.synthetic.main.sub_fragment_view_follow.*
import javax.inject.Inject

class ViewFollowerSubFragment @Inject constructor(
) : SyntheticFragment(R.layout.sub_fragment_view_follow) {

	private val viewModel: ViewFollowerSubViewModel by navGraphViewModels(R.id.viewFollowFragment) { viewModelProviderFactory }
	private val personId: Long by lazy {
		arguments?.getLong(ViewFollowFragment.KEY_PERSON_ID) ?: throw IllegalArgumentException()
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		sub_view_follow_edit_query.addTextChangedListener {
			requestFetchFollowersByKeyword(it?.toString())
		}

		val controller = FollowerController(
			onPersonClick = { person ->
				ViewFollowFragmentDirections
					.actionToClosetFragment(null, person.id)
					.navigate()
			}
		)
		sub_view_follow_list_person.setController(controller)
		viewModel.followers.observe(viewLifecycleOwner) {
			controller.submitList(it)
			controller.requestForcedModelBuild()
		}
		requestFetchFollowersByKeyword(null)
	}

	private fun requestFetchFollowersByKeyword(keyword: String?) {
		viewModel.requestFetchFollowersByKeyword(personId, keyword)
	}
}