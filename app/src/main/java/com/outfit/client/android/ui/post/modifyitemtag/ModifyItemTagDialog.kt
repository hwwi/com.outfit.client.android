package com.outfit.client.android.ui.post.modifyitemtag

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.outfit.client.android.R
import com.outfit.client.android.data.NetworkState
import com.outfit.client.android.data.args.ItemTagArgs
import com.outfit.client.android.extension.showError
import com.outfit.client.android.widget.AbstractDialog
import com.outfit.client.android.widget.controller.SearchGetPayloadController
import kotlinx.android.synthetic.main.dialog_modify_item_tag.*
import javax.inject.Inject

class ModifyItemTagDialog @Inject constructor(

) : AbstractDialog(R.layout.dialog_modify_item_tag) {
	private val viewModel: ModifyItemTagViewModel by viewModels { viewModelProviderFactory }
	private val args: ModifyItemTagDialogArgs by navArgs()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		dialog_modify_item_tag_edit_brand.filters += InputFilter.AllCaps()
		dialog_modify_item_tag_edit_product.filters += InputFilter.AllCaps()
		val textChangedObserver = Observer<NetworkState<Unit>> { validateState ->
			when (validateState) {
				is NetworkState.Fetching -> {
					dialog_modify_item_tag_progress_state.visibility = View.VISIBLE
					dialog_modify_item_tag_button_ok.isEnabled = false
					dialog_modify_item_tag_layout_brand.error = null
					dialog_modify_item_tag_layout_product.error = null
				}
				is NetworkState.Fail -> {
					dialog_modify_item_tag_progress_state.visibility = View.INVISIBLE
					dialog_modify_item_tag_button_ok.isEnabled = false
					showError(validateState.error) {
						when (it) {
							"brandCode" -> dialog_modify_item_tag_layout_brand
							"productCode" -> dialog_modify_item_tag_layout_product
							else -> null
						}
					}
				}
				is NetworkState.Success -> {
					dialog_modify_item_tag_progress_state.visibility = View.INVISIBLE
					dialog_modify_item_tag_button_ok.isEnabled = true
				}
			}
		}
		val okButtonClickObserver = object : Observer<NetworkState<Unit>> {
			override fun onChanged(validateState: NetworkState<Unit>) {
				textChangedObserver.onChanged(validateState)
				when (validateState) {
					is NetworkState.Fetching -> {
						dialog_modify_item_tag_layout_brand.isEnabled = false
						dialog_modify_item_tag_layout_product.isEnabled = false
					}
					is NetworkState.Fail -> {
						viewModel.validateState.removeObserver(this)
						viewModel.validateState.observe(viewLifecycleOwner, textChangedObserver)
						dialog_modify_item_tag_layout_brand.isEnabled = true
						dialog_modify_item_tag_layout_product.isEnabled = true
					}
					is NetworkState.Success -> {
						findNavController().previousBackStackEntry?.savedStateHandle?.set(
							ModifyItemTagViewModel.KEY_FILE_TO_ITEM_TAG_ARGS,
							args.file to ItemTagArgs(
								dialog_modify_item_tag_edit_brand.text.toString(),
								dialog_modify_item_tag_edit_product.text.toString(),
								args.x,
								args.y
							)
						)
						dismiss()
					}
				}
			}
		}

		dialog_modify_item_tag_button_ok.setOnClickListener {
			viewModel.validateState.removeObserver(textChangedObserver)
			viewModel.validateState.observe(viewLifecycleOwner, okButtonClickObserver)
			viewModel.validate(
				dialog_modify_item_tag_edit_brand.text?.toString(),
				dialog_modify_item_tag_edit_product.text?.toString()
			)
		}
		val textWatcher = object : TextWatcher {
			override fun afterTextChanged(s: Editable?) {
				val brandCode = dialog_modify_item_tag_edit_brand.text?.toString()
				val productCode = dialog_modify_item_tag_edit_product.text?.toString()
				viewModel.validate(brandCode, productCode)
				viewModel.search(brandCode, productCode)
			}

			override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

			override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
		}

		dialog_modify_item_tag_edit_brand.addTextChangedListener(textWatcher)
		dialog_modify_item_tag_edit_product.addTextChangedListener(textWatcher)
		dialog_modify_item_tag_button_cancel.setOnClickListener {
			dismiss()
		}

		viewModel.validateState.observe(viewLifecycleOwner, textChangedObserver)

		val controller = SearchGetPayloadController(
			useCategoryTitle = false
		).apply {
			onItemTagClickListener = { brandCode, productCode ->
				dialog_modify_item_tag_layout_brand.error = null
				dialog_modify_item_tag_layout_product.error = null
				if (dialog_modify_item_tag_edit_brand.text?.toString() != brandCode) {
					dialog_modify_item_tag_edit_brand.removeTextChangedListener(textWatcher)
					dialog_modify_item_tag_edit_brand.setText(brandCode)
					dialog_modify_item_tag_edit_brand.addTextChangedListener(textWatcher)
				}
				if (dialog_modify_item_tag_edit_product.text?.toString() != productCode) {
					dialog_modify_item_tag_edit_product.removeTextChangedListener(textWatcher)
					dialog_modify_item_tag_edit_product.setText(productCode)
					dialog_modify_item_tag_edit_product.addTextChangedListener(textWatcher)
				}
				viewModel.clearSearch()
			}
		}
		dialog_modify_item_tag_list_suggestion.setController(controller)
		viewModel.searchResult.observe(viewLifecycleOwner) {
			controller.setData(it)
		}
	}

	override fun onStart() {
		super.onStart()
		requireDialog().window?.setLayout(
			ViewGroup.LayoutParams.MATCH_PARENT,
			ViewGroup.LayoutParams.WRAP_CONTENT
		)
	}
}