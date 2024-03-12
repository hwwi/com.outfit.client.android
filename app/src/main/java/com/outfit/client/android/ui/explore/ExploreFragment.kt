package com.outfit.client.android.ui.explore

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.airbnb.epoxy.addGlidePreloader
import com.airbnb.epoxy.glidePreloader
import com.airbnb.epoxy.preload.ViewData
import com.airbnb.epoxy.preload.ViewMetadata
import com.bumptech.glide.RequestManager
import com.outfit.client.android.R
import com.outfit.client.android.data.CacheType
import com.outfit.client.android.data.NetworkState
import com.outfit.client.android.databinding.FragmentExploreBinding
import com.outfit.client.android.extension.showError
import com.outfit.client.android.glide.GlideApp
import com.outfit.client.android.util.autoCleared
import com.outfit.client.android.widget.DataBindingFragment
import com.outfit.client.android.widget.SyntheticFragment
import com.outfit.client.android.widget.adapter.LoadingStateInterceptor
import com.outfit.client.android.widget.controller.ShotThumbnailPagedController
import com.outfit.client.android.widget.item.ThumbnailItemModel_
import javax.inject.Inject


class ExploreFragment @Inject constructor(
) : DataBindingFragment<FragmentExploreBinding>(R.layout.fragment_explore) {
	private val viewModel: ExploreViewModel by activityViewModels { viewModelProviderFactory }
	private var controller: ShotThumbnailPagedController by autoCleared()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding.mergeToolbar.toolbar.menu.add("search").let { item ->
			item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM or MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW)
			item.setIcon(R.drawable.ic_search_24dp)

			item.setOnMenuItemClickListener {
				ExploreFragmentDirections
					.actionExploreFragmentToSearchFragment()
					.navigate()
				true
			}
		}

		controller = ShotThumbnailPagedController(
			onThumbnailClickListener = {
				ExploreFragmentDirections
					.actionExploreFragmentToViewShotFragment(it.id, CacheType.EXPLORE)
					.navigate()
			}
		)
		val loadingStateInterceptor = LoadingStateInterceptor(controller) {
			viewModel.retry()
		}
		binding.shotList.apply {
			setController(controller)
			addGlidePreloader(
				controller,
				GlideApp.with(this@ExploreFragment),
				preloader = glidePreloader { requestManager: RequestManager, epoxyModel: ThumbnailItemModel_, _: ViewData<ViewMetadata?> ->
					requestManager.load(epoxyModel.imageUri())
				}
			)
		}

		viewModel.shotList.observe(viewLifecycleOwner) {
			controller.submitList(it)
		}

		viewModel.shotListState.observe(viewLifecycleOwner){
			val state = it.getContentIfNotHandled() ?: return@observe
			loadingStateInterceptor.loadingState = state
		}

		viewModel.shotsRefreshState.observe(viewLifecycleOwner) {
			val state = it.getContentIfNotHandled() ?: return@observe
			binding.swipeRefresh.isRefreshing = state is NetworkState.Fetching
			when (state) {
				is NetworkState.Fetching -> loadingStateInterceptor.loadingState = null
				is NetworkState.Fail -> showError(state.error)
			}
		}

		binding.swipeRefresh.setOnRefreshListener {
			viewModel.refresh()
		}

//		if (!IntroducePref.isIntroduceExploreFragment)
//			MaterialAlertDialogBuilder(requireContext())
//				.setMessage(R.string.introduce_explore_fragment)
//				.setPositiveButton(R.string.str_ok) { _, _ ->
//					IntroducePref.isIntroduceExploreFragment = true
//				}
//				.show()
	}
}
