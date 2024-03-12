package com.outfit.client.android.ui.post.tagging

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.outfit.client.android.R
import com.outfit.client.android.data.args.ItemTagArgs
import com.outfit.client.android.ui.post.modifyitemtag.ModifyItemTagViewModel
import com.outfit.client.android.widget.SyntheticFragment
import com.outfit.client.android.widget.controller.FilesTagableImageController
import kotlinx.android.synthetic.main.fragment_post_tagging.*
import kotlinx.android.synthetic.main.merge_toolbar.*
import java.io.File
import javax.inject.Inject

class TaggingFragment @Inject constructor(
) : SyntheticFragment(R.layout.fragment_post_tagging) {

	private val viewModel: TaggingViewModel by viewModels { viewModelProviderFactory }
	private val args: TaggingFragmentArgs by navArgs()
	private var isSelectionSetted = false

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
		toolbar.menu.add(R.string.all_next).let { item ->
			item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
			item.setOnMenuItemClickListener {
				val value = viewModel.filePerItemTags.value
					?: return@setOnMenuItemClickListener true
				findNavController().apply {
					previousBackStackEntry?.savedStateHandle?.set(
						TaggingViewModel.KEY_FILE_PER_ITEM_TAGS,
						value
					)
					popBackStack()
				}
				true
			}
		}
		val navBackStackEntry = findNavController().getBackStackEntry(R.id.taggingFragment)
		val observer = object : LifecycleEventObserver {
			override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
				if (event != Lifecycle.Event.ON_RESUME)
					return
				val fileToItemTagArgs =
					navBackStackEntry.savedStateHandle.get<Pair<File, ItemTagArgs>>(
						ModifyItemTagViewModel.KEY_FILE_TO_ITEM_TAG_ARGS
					) ?: return
				viewModel.addTag(fileToItemTagArgs.first, fileToItemTagArgs.second)
			}
		}
		navBackStackEntry.lifecycle.addObserver(observer)
		viewLifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
			if (event == Lifecycle.Event.ON_DESTROY)
				navBackStackEntry.lifecycle.removeObserver(observer)
		})
		val controller =
			FilesTagableImageController<ItemTagArgs>(args.filePaths.map { File(it) }).apply {
				onTagAdd = { file, x, y ->
					TaggingFragmentDirections.actionTaggingFragmentToModifyItemTagDialog(file, x, y)
						.navigate()
				}
				onTagRemove = { file, tag ->
					viewModel.removeTagList(file, tag)
				}
			}
		post_tagging_list_image.adapter = controller.adapter
		viewModel.filePerItemTags.observe(viewLifecycleOwner) {
			controller.setData(it)
			if (!isSelectionSetted) {
				post_tagging_list_image.setCurrentItem(args.selectedUriPosition, false)
				isSelectionSetted = true
			}
		}
	}
}
