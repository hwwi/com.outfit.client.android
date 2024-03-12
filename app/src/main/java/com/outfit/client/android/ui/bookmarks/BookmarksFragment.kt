package com.outfit.client.android.ui.bookmarks

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.airbnb.epoxy.addGlidePreloader
import com.airbnb.epoxy.glidePreloader
import com.airbnb.epoxy.preload.ViewData
import com.airbnb.epoxy.preload.ViewMetadata
import com.bumptech.glide.RequestManager
import com.outfit.client.android.R
import com.outfit.client.android.data.CacheType
import com.outfit.client.android.data.NetworkState
import com.outfit.client.android.databinding.FragmentBookmarksBinding
import com.outfit.client.android.extension.showError
import com.outfit.client.android.glide.GlideApp
import com.outfit.client.android.util.autoCleared
import com.outfit.client.android.widget.DataBindingFragment
import com.outfit.client.android.widget.SyntheticFragment
import com.outfit.client.android.widget.adapter.LoadingStateInterceptor
import com.outfit.client.android.widget.controller.ShotThumbnailPagedController
import com.outfit.client.android.widget.item.ThumbnailItemModel_
import javax.inject.Inject


class BookmarksFragment @Inject constructor(
) : DataBindingFragment<FragmentBookmarksBinding>(R.layout.fragment_bookmarks) {
	private val viewModel: BookmarksViewModel by viewModels { viewModelProviderFactory }
	private var controller: ShotThumbnailPagedController by autoCleared()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		controller = ShotThumbnailPagedController(
			onThumbnailClickListener = {
				BookmarksFragmentDirections
					.actionBookmarksFragmentToViewShotFragment(it.id, CacheType.BOOKMARKS)
					.navigate()
			}
		)
		val loadingStateInterceptor = LoadingStateInterceptor(controller) {
			viewModel.retry()
		}

		binding.listShot.apply {
			setController(controller)
			addGlidePreloader(
				controller,
				GlideApp.with(this@BookmarksFragment),
				preloader = glidePreloader { requestManager: RequestManager, epoxyModel: ThumbnailItemModel_, _: ViewData<ViewMetadata?> ->
					requestManager.load(epoxyModel.imageUri())
				}
			)
		}

		viewModel.shotList.observe(viewLifecycleOwner) {
			controller.submitList(it)
			if (it.isNotEmpty()) {
				binding.error.clearError()
				return@observe
			}
			binding.error.setError(
				R.string.error_title_bookmarks_is_empty,
				R.string.error_msg_bookmarks_is_empty
			)
		}

		viewModel.shotListState.observe(viewLifecycleOwner) {
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
	}
}
