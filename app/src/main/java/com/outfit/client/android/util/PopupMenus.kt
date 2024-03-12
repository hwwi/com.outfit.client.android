package com.outfit.client.android.util

import android.content.ClipData
import android.content.ClipboardManager
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.outfit.client.android.MainNavDirections
import com.outfit.client.android.R
import com.outfit.client.android.data.model.Comment
import com.outfit.client.android.data.model.Shot
import com.outfit.client.android.data.vo.TagDisplayable
import com.outfit.client.android.extension.showError
import com.outfit.client.android.repository.ShotRepository
import com.outfit.client.android.ui.viewshot.ViewShotFragmentDirections
import kotlinx.coroutines.launch

object PopupMenus {
	fun showOnTagClick(fragment: Fragment, anchor: View, tag: TagDisplayable) {
		val context = fragment.requireContext()
		PopupMenu(context, anchor).apply {
			menu.add(R.string.msg_copy_item_tag_to_clipboard).setOnMenuItemClickListener {
				val clipboardManager = context.getSystemService<ClipboardManager>()
				if (clipboardManager != null) {
					val itemTagClip = when {
						tag.product.isNullOrBlank() -> fragment.getString(
							R.string.tag_brand,
							tag.brand
						)
						else -> fragment.getString(R.string.tag_product, tag.brand, tag.product)
					}.let {
						ClipData.newPlainText(
							"itemTag",
							it
						)
					}
					clipboardManager.setPrimaryClip(itemTagClip)
				}
				true
			}
			menu.add(R.string.msg_search_brand).setOnMenuItemClickListener {
				fragment.findNavController()
					.navigate(
						MainNavDirections.actionToViewItemTagFragment(tag.brand)
					)
				true
			}
			if (!tag.product.isNullOrBlank())
				menu.add(R.string.msg_search_product).setOnMenuItemClickListener {
					fragment.findNavController()
						.navigate(
							MainNavDirections.actionToViewItemTagFragment(tag.brand, tag.product)
						)
					true
				}
			show()
		}
	}

	fun showShotOwner(
		fragment: Fragment,
		anchor: View,
		shot: Shot,
		shotRepository: ShotRepository
	) {
		PopupMenu(fragment.requireContext(), anchor)
			.apply {
				when (shot.isPrivate) {
					true -> menu.add(R.string.msg_move_to_closet)
						.setOnMenuItemClickListener {
							fragment.viewLifecycleOwner.lifecycleScope.launch {
								try {
									shotRepository.deletePrivate(shot.id)
								} catch (e: Exception) {
									fragment.showError(e)
								}
							}
							true
						}
					false -> menu.add(R.string.msg_move_to_private_closet)
						.setOnMenuItemClickListener {
							fragment.viewLifecycleOwner.lifecycleScope.launch {
								try {
									shotRepository.putPrivate(shot.id)
								} catch (e: Exception) {
									fragment.showError(e)
								}
							}
							true
						}
				}
				menu.add(R.string.str_delete).setOnMenuItemClickListener {
					fragment.findNavController()
						.navigate(MainNavDirections.actionToDeleteShotConfirmDialog(shot.id))
					true
				}
			}
			.show()
	}

	fun showCommentOwner(fragment: Fragment, anchor: View, comment: Comment) {
		PopupMenu(fragment.requireContext(), anchor)
			.apply {
				menu.add(R.string.str_delete).setOnMenuItemClickListener {
					fragment.findNavController()
						.navigate(
							MainNavDirections.actionToDeleteCommentConfirmDialog(
								comment.shotId,
								comment.id
							)
						)
					true
				}
			}
			.show()
	}
}