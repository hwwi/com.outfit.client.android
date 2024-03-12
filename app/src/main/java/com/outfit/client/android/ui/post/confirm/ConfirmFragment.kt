package com.outfit.client.android.ui.post.confirm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.outfit.client.android.R
import com.outfit.client.android.data.NetworkState
import com.outfit.client.android.data.PopUpTo
import com.outfit.client.android.data.SelectImagePurpose
import com.outfit.client.android.extension.asHtml
import com.outfit.client.android.extension.showError
import com.outfit.client.android.pref.IntroducePref
import com.outfit.client.android.widget.SyntheticFragment
import com.outfit.client.android.widget.controller.FilesThumbnailController
import kotlinx.android.synthetic.main.fragment_post_confirm.*
import kotlinx.android.synthetic.main.merge_toolbar.*
import javax.inject.Inject

class ConfirmFragment @Inject constructor(
) : SyntheticFragment(R.layout.fragment_post_confirm) {
	private val viewModel: ConfirmViewModel by navGraphViewModels(R.id.post_navigation) { viewModelProviderFactory }


	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		if (!viewModel.isImageSelected) {
			navigateToSelectImageFragment()
			return null
		}
		return super.onCreateView(inflater, container, savedInstanceState)
	}

	override fun onViewCreated(view: View, savedInstapostNewShotnceState: Bundle?) {
		val menuItemShare: MenuItem = toolbar.menu.add(R.string.str_share).let { item ->
			item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
			item.setOnMenuItemClickListener {
				when (IntroducePref.isWarningPostConfirmFragmentPostPolicy) {
					true -> postNewShot()
					false -> {
						MaterialAlertDialogBuilder(requireContext())
							.setMessage(R.string.warning_post_confirm_fragment_post_policy)
							.setPositiveButton(R.string.str_ok) { _, _ ->
								postNewShot()
								IntroducePref.isWarningPostConfirmFragmentPostPolicy = true
							}
							.setNegativeButton(R.string.str_cancel, null)
							.show()
					}
				}
				true
			}
		}

		val controller = FilesThumbnailController(RecyclerView.HORIZONTAL).apply {
			onThumbnailClickListener = { index ->
				findNavController().navigate(
					ConfirmFragmentDirections.actionConfirmFragmentToTaggingFragment(
						viewModel.files.map { it.absolutePath }.toTypedArray(),
						index
					)
				)
			}
		}

		rv_selected_image.setController(controller)

		controller.setData(viewModel.files, true)

		viewModel.filePerItemTags.observe(viewLifecycleOwner) { filePerItemTags ->
			post_confirm_text_header.text = getString(
				R.string.html_click_image_to_add_item_tag_d,
				filePerItemTags.values.sumBy { it.size }
			)
				.asHtml()

		}

		viewModel.networkState.observe(viewLifecycleOwner) { state ->
			when (state) {
				is NetworkState.Fetching -> {
					progress.visibility = View.VISIBLE
					controller.setData(viewModel.files, false)
					confirm_layout_caption.isEnabled = false
					menuItemShare.isEnabled = false
				}
				is NetworkState.Fail -> {
					progress.visibility = View.INVISIBLE
					controller.setData(viewModel.files, true)
					confirm_layout_caption.isEnabled = true
					menuItemShare.isEnabled = true
					showError(state.error) {
						when (it) {
							"args.Caption" -> confirm_layout_caption
							else -> null
						}
					}
				}
				is NetworkState.Success -> {
					progress.visibility = View.INVISIBLE
					findNavController().popBackStack(R.id.post_navigation, true)
				}
			}
		}
		toolbar.setNavigationOnClickListener {
			onBackPressed()
		}
		requireActivity().onBackPressedDispatcher.addCallback(this) {
			onBackPressed()
		}
	}

	private fun onBackPressed() {
		et_caption.text = null
		viewModel.clearFiles()
		navigateToSelectImageFragment()
	}

	private fun postNewShot() {
		viewModel.postNewShot(et_caption.text.toString())
	}

	private fun navigateToSelectImageFragment() {
		ConfirmFragmentDirections
			.actionConfirmFragmentToSelectImageNav(
				SelectImagePurpose.Shot,
				PopUpTo(R.id.post_navigation, true)
			)
			.navigate()
	}
}
