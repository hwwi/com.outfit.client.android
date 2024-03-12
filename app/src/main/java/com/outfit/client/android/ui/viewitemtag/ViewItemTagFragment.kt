package com.outfit.client.android.ui.viewitemtag

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.airbnb.epoxy.addGlidePreloader
import com.airbnb.epoxy.glidePreloader
import com.airbnb.epoxy.preload.ViewData
import com.airbnb.epoxy.preload.ViewMetadata
import com.bumptech.glide.RequestManager
import com.outfit.client.android.R
import com.outfit.client.android.glide.GlideApp
import com.outfit.client.android.util.autoCleared
import com.outfit.client.android.widget.SyntheticFragment
import com.outfit.client.android.widget.controller.ShotThumbnailPagedController
import com.outfit.client.android.widget.item.ThumbnailItemModel_
import kotlinx.android.synthetic.main.fragment_view_item_tag.*
import kotlinx.android.synthetic.main.merge_toolbar.*
import javax.inject.Inject


class ViewItemTagFragment @Inject constructor(
) : SyntheticFragment(R.layout.fragment_view_item_tag) {
	private val viewModel: ViewItemTagViewModel by viewModels { viewModelProviderFactory }
	private val args: ViewItemTagFragmentArgs by navArgs()
	private var controller: ShotThumbnailPagedController by autoCleared()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		val context = requireContext()

		toolbar.title = when {
			args.productCode.isNullOrBlank() -> getString(R.string.tag_brand, args.brandCode)
			else -> getString(R.string.tag_product, args.brandCode, args.productCode)
		}

		controller = ShotThumbnailPagedController(
			onThumbnailClickListener = {
				ViewItemTagFragmentDirections
					.actionViewItemTagFragmentToViewShotFragment(it.id, null)
					.navigate()
			}
		).apply {
			spanCount = 3
		}
		view_item_tag_list_shot.apply {
			layoutManager = GridLayoutManager(context, controller.spanCount).apply {
				spanSizeLookup = controller.spanSizeLookup
			}
			adapter = controller.adapter
			setHasFixedSize(true)
			addGlidePreloader(
				controller,
				GlideApp.with(this@ViewItemTagFragment),
				preloader = glidePreloader { requestManager: RequestManager, epoxyModel: ThumbnailItemModel_, _: ViewData<ViewMetadata?> ->
					requestManager.load(epoxyModel.imageUri())
				}
			)
		}

		viewModel.shots.observe(viewLifecycleOwner) {
			controller.submitList(it)
		}

//		observeNetworkState(viewModel.shotsNetworkState) {
//			viewModel.retry()
//		}

		viewModel.shotsRefreshState.observe(viewLifecycleOwner) {
			view_item_tag_swipe_refresh.isRefreshing = it
		}

		view_item_tag_swipe_refresh.setOnRefreshListener {
			viewModel.refresh()
		}
	}
}
