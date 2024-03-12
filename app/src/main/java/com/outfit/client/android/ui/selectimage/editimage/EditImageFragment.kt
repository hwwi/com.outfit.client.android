package com.outfit.client.android.ui.selectimage.editimage

import android.graphics.Rect
import android.os.Bundle
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.airbnb.epoxy.EpoxyItemSpacingDecorator
import com.airbnb.epoxy.addGlidePreloader
import com.airbnb.epoxy.glidePreloader
import com.google.android.material.button.MaterialButton
import com.outfit.client.android.R
import com.outfit.client.android.glide.GlideApp
import com.outfit.client.android.ui.selectimage.SelectImageNav
import com.outfit.client.android.widget.SyntheticFragment
import com.outfit.client.android.widget.item.ThumbnailItemModel_
import kotlinx.android.synthetic.main.fragment_post_edit_image.*
import kotlinx.android.synthetic.main.merge_toolbar.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EditImageFragment @Inject constructor(
) : SyntheticFragment(R.layout.fragment_post_edit_image) {
	private val viewModel: EditImageViewModel by viewModels { viewModelProviderFactory }
	private val args: EditImageFragmentArgs by navArgs()

	private var highlightColor: Int = 0
	private var nonHighlightColor: Int = 0

	private fun updateRatioAndRect() {
		val cropRect: Rect? = post_edit_image_image_selected.cropRect
		if (cropRect != null)
			viewModel.updateCropRect(cropRect)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		val typedValue = TypedValue()
		view.context.theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
		highlightColor = typedValue.data
		view.context.theme.resolveAttribute(R.attr.colorPrimaryVariant, typedValue, true)
		nonHighlightColor = typedValue.data
		toolbar.menu.add(R.string.all_next).apply {
			setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
			setOnMenuItemClickListener {
				viewLifecycleOwner.lifecycleScope.launch {
					it.isEnabled = false
					updateRatioAndRect()
					val uris = viewModel.getSelectedCroppedFiles()
					findNavController().apply {
						getBackStackEntry(args.requestDestinationId)
							.savedStateHandle
							.apply {
								this[SelectImageNav.KEY_PURPOSE] = args.purpose
								this[SelectImageNav.KEY_CACHE_FILES] = uris
							}
						popBackStack(R.id.selectImageNav, true)
					}
					it.isEnabled = true
				}
				true
			}
		}

		post_edit_image_list_preview.visibility = when (args.uris.size) {
			0, 1 -> View.GONE
			else -> View.VISIBLE
		}

		val ratioPerButtonMap = when {
			args.purpose.selectableRatios.size in 0..1
				// 선택된 이미지가 2개 이상이면 purpose.defaultRatio 고정
				|| args.uris.size > 1 -> mapOf()
			else -> {
				args.purpose.selectableRatios.associateWith {
					MaterialButton(requireContext(), null, R.attr.materialButtonTextStyle).apply {
						layoutParams = LinearLayout.LayoutParams(
							0,
							LinearLayout.LayoutParams.WRAP_CONTENT,
							1f
						)
						text = it.displayText
						setOnClickListener { _ ->
							viewModel.select(it)
						}
						post_edit_image_layout_ratio.addView(this)
					}
				}
			}
		}

		val controller = PreviewController().apply {
			onThumbnailClickListener = fun(uri) {
				updateRatioAndRect()
				viewModel.select(uri)
			}
			setData(args.uris)
		}

		post_edit_image_list_preview.apply {
			setController(controller)
			addItemDecoration(EpoxyItemSpacingDecorator(4))
			addGlidePreloader(
				controller,
				GlideApp.with(this@EditImageFragment),
				preloader = glidePreloader { requestManager, epoxyModel: ThumbnailItemModel_, _ ->
					requestManager.load(epoxyModel.imageUri())
				}
			)
		}
		viewModel.selectedUriAndCropRect.observe(viewLifecycleOwner) {
			viewLifecycleOwner.lifecycleScope.launch {
				val bitmap = withContext(Dispatchers.IO) {
					GlideApp.with(post_edit_image_image_selected)
						.asBitmap()
						.load(it.first)
						.submit()
						.get()
				}

				post_edit_image_image_selected.setImageBitmap(bitmap)

				val rect = it.second
				if (rect != null) {
					post_edit_image_image_selected.apply {
						isAutoZoomEnabled = false
						cropRect = rect
						isAutoZoomEnabled = true
					}
				}
			}
		}
		viewModel.currentAspectRatio.observe(viewLifecycleOwner) { value ->
			post_edit_image_image_selected.apply {
				isAutoZoomEnabled = false
				setAspectRatio(value.x, value.y)
				cropRect = null
				isAutoZoomEnabled = true
			}
			ratioPerButtonMap.entries.forEach {
				it.value.setTextColor(
					when (it.key) {
						value -> highlightColor
						else -> nonHighlightColor
					}
				)
			}
		}
	}
}