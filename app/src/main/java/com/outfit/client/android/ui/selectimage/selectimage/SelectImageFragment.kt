package com.outfit.client.android.ui.selectimage.selectimage

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.airbnb.epoxy.*
import com.airbnb.epoxy.paging.PagedListEpoxyController
import com.airbnb.epoxy.preload.ViewData
import com.airbnb.epoxy.preload.ViewMetadata
import com.bumptech.glide.RequestManager
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.outfit.client.android.R
import com.outfit.client.android.data.vo.BucketEntry
import com.outfit.client.android.extension.longToast
import com.outfit.client.android.extension.toast
import com.outfit.client.android.glide.GlideApp
import com.outfit.client.android.util.autoCleared
import com.outfit.client.android.widget.SyntheticFragment
import com.outfit.client.android.widget.adapter.AbstractBaseAdapter
import com.outfit.client.android.widget.adapter.ViewHolderContainer
import com.outfit.client.android.widget.item.ThumbnailItemModel_
import com.outfit.client.android.widget.item.thumbnailItem
import kotlinx.android.synthetic.main.action_select_image.view.*
import kotlinx.android.synthetic.main.fragment_post_select_image.*
import kotlinx.android.synthetic.main.item_simple_spinner.*
import kotlinx.android.synthetic.main.merge_toolbar.*
import javax.inject.Inject

class SelectImageFragment @Inject constructor(

) : SyntheticFragment(R.layout.fragment_post_select_image) {
	private val viewModel: SelectImageViewModel by navGraphViewModels(R.id.selectImageNav) { viewModelProviderFactory }
	private val args: SelectImageFragmentArgs by navArgs()
	private var bucketAdapter by autoCleared<AbstractBaseAdapter<BucketEntry?>>()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		bucketAdapter = object : AbstractBaseAdapter<BucketEntry?>(
			requireContext(),
			R.layout.item_simple_spinner
		) {
			override fun onBindView(
				context: Context,
				holder: ViewHolderContainer,
				item: BucketEntry?,
				position: Int
			) {
				holder.text1.text = item?.name ?: getString(R.string.str_view_all)
			}
		}

		val previewController = SelectableController().apply {
			spanCount = 4
		}

		post_select_image_list_preview.apply {
			setController(previewController)
			layoutManager = GridLayoutManager(context, previewController.spanCount).apply {
				spanSizeLookup = previewController.spanSizeLookup
			}
			addItemDecoration(EpoxyItemSpacingDecorator(4))
			addGlidePreloader(
				previewController,
				GlideApp.with(this@SelectImageFragment),
				preloader = glidePreloader { requestManager: RequestManager, epoxyModel: ThumbnailItemModel_, _: ViewData<ViewMetadata?> ->
					requestManager.load(epoxyModel.imageUri())
				}
			)
		}
		val selectedController = SelectedController()
		post_select_image_list_selected.apply {
			setController(selectedController)
			setPaddingDp(8)
			addItemDecoration(EpoxyItemSpacingDecorator(8))
			addGlidePreloader(
				previewController,
				GlideApp.with(this@SelectImageFragment),
				preloader = glidePreloader { requestManager: RequestManager, epoxyModel: ThumbnailItemModel_, _: ViewData<ViewMetadata?> ->
					requestManager.load(epoxyModel.imageUri())
				}
			)
		}

		val actionView = layoutInflater.inflate(R.layout.action_select_image, null).apply {
			action_select_image_list_bucket.apply {
				adapter = bucketAdapter
				onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
					override fun onNothingSelected(parent: AdapterView<*>?) {}

					override fun onItemSelected(
						parent: AdapterView<*>?,
						view: View?,
						position: Int,
						id: Long
					) {
						bucketAdapter.getItem(position).let { bucketEntry ->
							viewModel.selectBucket(bucketEntry)
						}
					}
				}
			}

			action_select_image_button_next.setOnClickListener {
				val selectedUris = viewModel.selectedUris.value
				when {
					selectedUris.isNullOrEmpty() -> toast(R.string.msg_nothing_selected)
					else -> findNavController().apply {
						val requestDestinationId =
							previousBackStackEntry!!.destination.id
						navigate(
							SelectImageFragmentDirections
								.actionSelectImageFragmentToEditImageFragment(
									args.purpose,
									requestDestinationId,
									selectedUris.toTypedArray()
								)
						)
					}
				}
			}
		}
		toolbar.menu.add("").let { item ->
			item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
			item.actionView = actionView
		}

		viewModel.outputImages.observe(viewLifecycleOwner) {
			previewController.submitList(it)
		}

		viewModel.selectedUris.observe(viewLifecycleOwner) {
			previewController.submitSelection(it)
			selectedController.setData(it)
		}

		toolbar.setNavigationOnClickListener {
			onBackPressed()
		}
		requireActivity().onBackPressedDispatcher.addCallback(this) {
			if (!viewModel.selectedUris.value.isNullOrEmpty()) {
				viewModel.clearSelection()
				return@addCallback
			}
			onBackPressed()
		}

		Dexter.withActivity(activity)
			.withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
			.withListener(object : PermissionListener {
				override fun onPermissionGranted(response: PermissionGrantedResponse) {
					viewModel.outputExternalImagesBucketPaths.observe(viewLifecycleOwner) { pair ->
						val bucketEntries = pair.first
						val lastSelectedBucketId = pair.second

						if (bucketEntries.isEmpty())
							longToast(R.string.all_no_images_available)
						bucketAdapter.clear()
						bucketAdapter.addAll(bucketEntries)

						if (lastSelectedBucketId != null) {
							val lastSelectedBucketIndex =
								bucketEntries.indexOfFirst { it?.id == lastSelectedBucketId }
							if (lastSelectedBucketIndex != -1)
								actionView.action_select_image_list_bucket.setSelection(
									lastSelectedBucketIndex
								)
						}
					}
				}

				override fun onPermissionRationaleShouldBeShown(
					permission: PermissionRequest,
					token: PermissionToken
				) {
					Snackbar.make(
						actionView.action_select_image_list_bucket,
						R.string.permission_read_external_storage,
						Snackbar.LENGTH_INDEFINITE
					)
						.setAction(R.string.str_ok) {
							token.continuePermissionRequest()
						}
						.show()
				}

				override fun onPermissionDenied(response: PermissionDeniedResponse) {
				}
			})
			.check()
	}

	private fun onBackPressed() {
		when (val popUpTo = args.popUpTo) {
			null -> findNavController().popBackStack()
			else -> findNavController().popBackStack(
				popUpTo.destinationId,
				popUpTo.inclusive
			)
		}
	}


	inner class SelectableController : PagedListEpoxyController<Uri>() {
		private var _currentSelection: List<Uri>? = null

		fun submitSelection(selection: List<Uri>) {
			_currentSelection = selection
			requestForcedModelBuild()
		}

		override fun buildItemModel(currentPosition: Int, item: Uri?): EpoxyModel<*> =
			ThumbnailItemModel_().apply {
				id(currentPosition)
				image(item)
				val index = _currentSelection?.indexOf(item)
				selected(index != null && index != -1)
				selectedNumber(
					when (index) {
						null,
						-1 -> -1
						else -> index + 1
					}
				)
				onThumbnailClickListener { model, _, _, _ ->
					val uri = model.imageUri() ?: return@onThumbnailClickListener
					viewModel.toggleSelection(uri)
				}
			}
	}

	inner class SelectedController : TypedEpoxyController<List<Uri>>() {

		override fun buildModels(data: List<Uri>?) {
			data?.forEach { item ->
				thumbnailItem {
					id(item.hashCode())
					withWrapWidthMatchHeightLayout()
					image(item)
					cornerButtonIcon(R.drawable.ic_close_circle)
					onCornerButtonClickListener { model, _, _, _ ->
						val uri = model.imageUri() ?: return@onCornerButtonClickListener
						viewModel.deleteSelection(uri)
					}
				}
			}
		}
	}
}