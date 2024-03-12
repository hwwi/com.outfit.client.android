package com.outfit.client.android.ui.editprofile

import android.os.Bundle
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.outfit.client.android.R
import com.outfit.client.android.data.NetworkState
import com.outfit.client.android.data.SelectImagePurpose
import com.outfit.client.android.databinding.FragmentEditProfileBinding
import com.outfit.client.android.extension.showError
import com.outfit.client.android.extension.toast
import com.outfit.client.android.glide.GlideApp
import com.outfit.client.android.ui.selectimage.SelectImageNav
import com.outfit.client.android.widget.DataBindingFragment
import java.io.File
import javax.inject.Inject

class EditProfileFragment @Inject constructor(
) : DataBindingFragment<FragmentEditProfileBinding>(R.layout.fragment_edit_profile) {
	private val viewModel: EditProfileViewModel by viewModels { viewModelProviderFactory }

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding.networkState = viewModel.submitState
		val menuItemSubmit = binding.mergeToolbar.toolbar.menu.add(R.string.str_submit).apply {
			setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
			setOnMenuItemClickListener {
				viewModel.submitChanges(
					binding.editPersonBiography.text?.toString()
				)
				true
			}
		}

		viewModel.personDetail.observe(viewLifecycleOwner) { personDetail ->
			menuItemSubmit.isEnabled = true
			binding.textPersonName.text = personDetail.name
			binding.editPersonBiography.setText(personDetail.biography)
			binding.buttonChangePersonName.setOnClickListener {
				EditProfileFragmentDirections
					.actionEditProfileFragmentToChangeNameFragment()
					.navigate()
			}
			binding.buttonChangeClosetBackground.setOnClickListener {
				PopupMenu(requireContext(), it)
					.apply {
						menu.add(R.string.msg_new_background).setOnMenuItemClickListener {
							EditProfileFragmentDirections
								.actionEditProfileFragmentToSelectImageNav(SelectImagePurpose.ClosetBackground)
								.navigate()
							true
						}

						if (personDetail.closetBackgroundImageUrl != null)
							menu.add(R.string.msg_remove_background).setOnMenuItemClickListener {
								EditProfileFragmentDirections
									.actionEditProfileFragmentToDeleteViewerClosetBackgroundImageDialog()
									.navigate()
								true
							}
					}
					.show()
			}
			binding.buttonPersonProfile.setOnClickListener {
				PopupMenu(requireContext(), it)
					.apply {
						menu.add(R.string.msg_new_profile_photo).setOnMenuItemClickListener {
							EditProfileFragmentDirections
								.actionEditProfileFragmentToSelectImageNav(SelectImagePurpose.Profile)
								.navigate()
							true
						}
						if (personDetail.profileImageUrl != null)
							menu.add(R.string.msg_remove_profile_photo).setOnMenuItemClickListener {
								EditProfileFragmentDirections
									.actionEditProfileFragmentToDeleteViewerProfileImageDialog()
									.navigate()
								true
							}
					}
					.show()
			}
			GlideApp.with(this).apply {
				load(personDetail.closetBackgroundImageUrl)
					.into(binding.imageClosetBackground)
				load(personDetail.profileImageUrl)
					.apply(
						RequestOptions.bitmapTransform(
							RoundedCorners(
								TypedValue.applyDimension(
									TypedValue.COMPLEX_UNIT_DIP,
									4f,
									resources.displayMetrics
								).toInt()
							)
						)
					)
					.fallback(R.drawable.ic_person_24dp)
					.error(R.drawable.ic_error_outline_24dp)
					.into(binding.imagePersonProfile)
			}
		}

		viewModel.personDetailState.observe(viewLifecycleOwner) {
			if (it is NetworkState.Fail) {
				menuItemSubmit.isEnabled = false
				binding.textPersonName.text = null
				binding.editPersonBiography.text = null
				GlideApp.with(this).apply {
					clear(binding.imageClosetBackground)
					clear(binding.imagePersonProfile)
				}
				showError(it.error)
			}
		}

		viewModel.submitState.observe(viewLifecycleOwner) { networkState ->
			when (networkState) {
				is NetworkState.Fetching -> {
					menuItemSubmit.isEnabled = false
					binding.layoutPersonBiography.error = null
				}
				is NetworkState.Success -> {
					menuItemSubmit.isEnabled = true
					toast(R.string.msg_has_been_applied)
				}
				is NetworkState.Fail -> {
					menuItemSubmit.isEnabled = true
					showError(networkState.error) {
						when (it) {
							"biography" -> binding.layoutPersonBiography
							else -> null
						}
					}
				}
			}
		}

		findNavController().currentBackStackEntry?.savedStateHandle?.apply {
			val purpose = remove<SelectImagePurpose>(SelectImageNav.KEY_PURPOSE)
			val files = remove<List<File>>(SelectImageNav.KEY_CACHE_FILES)
			if (purpose == null || files == null || files.isEmpty()) return@apply
			val path = files.first().absolutePath
			when (purpose) {
				SelectImagePurpose.Profile -> EditProfileFragmentDirections
					.actionEditProfileFragmentToChangeViewerProfileImageDialog(path)
					.navigate()
				SelectImagePurpose.ClosetBackground -> EditProfileFragmentDirections
					.actionEditProfileFragmentToChangeViewerClosetBackgroundImageDialog(path)
					.navigate()
			}
		}
	}

}