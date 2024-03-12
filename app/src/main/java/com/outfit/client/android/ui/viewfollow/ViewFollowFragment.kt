package com.outfit.client.android.ui.viewfollow

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.outfit.client.android.R
import com.outfit.client.android.data.NetworkState
import com.outfit.client.android.di.DaggerFragmentFactory
import com.outfit.client.android.extension.showError
import com.outfit.client.android.widget.SyntheticFragment
import kotlinx.android.synthetic.main.fragment_view_follow.*
import kotlinx.android.synthetic.main.merge_toolbar.*
import javax.inject.Inject


class ViewFollowFragment @Inject constructor(
	private val daggerFragmentFactory: DaggerFragmentFactory
) : SyntheticFragment(R.layout.fragment_view_follow) {
	companion object {
		const val KEY_PERSON_ID =
			"com.outfit.client.android.ui.viewfollow.ViewFollowFragment.KEY_PERSON_ID"
	}

	private val viewModel: ViewFollowViewModel by viewModels { viewModelProviderFactory }
	private val args: ViewFollowFragmentArgs by navArgs()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		val fragmentAdapter = object : FragmentStateAdapter(this) {
			override fun getItemCount(): Int = 2

			override fun createFragment(position: Int): Fragment =
				daggerFragmentFactory.instantiate(
					when (position) {
						0 -> ViewFollowerSubFragment::class.java
						else -> ViewFollowingSubFragment::class.java
					}
				)!!.apply {
					arguments = bundleOf(KEY_PERSON_ID to args.personId)
				}
		}

		view_follow_pager.apply {
			adapter = fragmentAdapter
		}
		TabLayoutMediator(view_follow_layout_tab, view_follow_pager) { tab, position ->
			tab.setText(
				when (position) {
					0 -> R.string.str_follower
					else -> R.string.str_following
				}
			)
		}.attach()
		if (args.viewFollowingFirst)
			view_follow_pager.setCurrentItem(1, false)

		viewModel.person.observe(viewLifecycleOwner) {
			toolbar.title = it.name
		}
		viewModel.personState.observe(viewLifecycleOwner) {
			if (it is NetworkState.Fail)
				showError(it.error)
		}
	}
}
