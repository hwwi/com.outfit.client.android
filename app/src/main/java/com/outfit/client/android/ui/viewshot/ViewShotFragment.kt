package com.outfit.client.android.ui.viewshot

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.outfit.client.android.R
import com.outfit.client.android.data.NetworkState
import com.outfit.client.android.extension.alertErrorAndNavigateUp
import com.outfit.client.android.extension.showError
import com.outfit.client.android.pref.SessionPref
import com.outfit.client.android.repository.CommentRepository
import com.outfit.client.android.repository.ShotRepository
import com.outfit.client.android.text.style.OnTagSpansClickListener
import com.outfit.client.android.ui.deleteshot.DeleteShotConfirmDialog
import com.outfit.client.android.util.PopupMenus
import com.outfit.client.android.util.autoCleared
import com.outfit.client.android.widget.SyntheticFragment
import com.outfit.client.android.widget.controller.CommentPagedController
import kotlinx.android.synthetic.main.fragment_view_shot.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class ViewShotFragment @Inject constructor(
	private val shotRepository: ShotRepository,
	private val commentRepository: CommentRepository
) : SyntheticFragment(R.layout.fragment_view_shot) {
	private val viewModel: ViewShotViewModel by viewModels { viewModelProviderFactory }
	private val args: ViewShotFragmentArgs by navArgs()

	private var commentController: CommentPagedController by autoCleared()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		closet_time_line_item.onCommentButtonClickListener {
			ViewShotFragmentDirections
				.actionViewShotFragmentToPostCommentDialog(args.shotId, null)
				.navigate()
		}
		closet_time_line_item.setCommentButtonText(getString(R.string.msg_click_to_comment))
		closet_time_line_item.onTagClickListener { v, tagDisplayable ->
			PopupMenus.showOnTagClick(this, v, tagDisplayable)
		}
		val onTagSpansClickListener = object : OnTagSpansClickListener {
			override fun onPersonTagClick(name: String) {
				ViewShotFragmentDirections
					.actionViewShotFragmentToClosetFragment(name)
					.navigate()
			}

			override fun onItemTagClick(brand: String, product: String?) {
				ViewShotFragmentDirections
					.actionViewShotFragmentToViewItemTagFragment(brand, product)
					.navigate()
			}

			override fun onHashTagClick(tag: String) {
				ViewShotFragmentDirections
					.actionViewShotFragmentToViewHashTagFragment(tag)
					.navigate()
			}
		}
		closet_time_line_item.onCaptionTagSpansClickListener = onTagSpansClickListener
		commentController = CommentPagedController(
			onCommentReplyClickListener = { comment ->
				ViewShotFragmentDirections
					.actionViewShotFragmentToPostCommentDialog(
						args.shotId,
						comment
					)
					.navigate()
			},
			onCommentMenuClickListener = { v, comment ->
				when (comment.person.id) {
					SessionPref.id -> PopupMenus.showCommentOwner(
						this@ViewShotFragment,
						v,
						comment
					)
//					else -> PopupMenus.showComment(this@ViewShotFragment, v, comment)
				}
			},
			onCommentLikeClickListener = { v, comment ->
				viewLifecycleOwner.lifecycleScope.launch {
					v.isEnabled = false
					try {
						when (comment.isViewerLike) {
							true -> commentRepository.deleteLike(comment.shotId, comment.id)
							false -> commentRepository.putLike(comment.shotId, comment.id)
						}
					} catch (e: Exception) {
						showError(e)
					} finally {
						v.isEnabled = false
					}
				}
			},
			onRetryButtonClickListener = {
				viewModel.retryComments()
			},
			onTagSpansClickListener = onTagSpansClickListener
		)
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
			list_comment.isNestedScrollingEnabled = false
		}
		list_comment.setItemSpacingDp(8)
		list_comment.setController(commentController)
		swipe_refresh_comment.setOnRefreshListener {
			viewModel.refresh()
		}

		val navBackStackEntry = findNavController().getBackStackEntry(R.id.viewShotFragment)
		val observer = object : LifecycleEventObserver {
			override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
				if (event != Lifecycle.Event.ON_RESUME)
					return
				val isDeleted = navBackStackEntry.savedStateHandle
					.get<Boolean>(DeleteShotConfirmDialog.KEY_IS_DELETED) ?: return
				if (isDeleted)
					findNavController().popBackStack()
			}
		}
		navBackStackEntry.lifecycle.addObserver(observer)
		viewLifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
			if (event == Lifecycle.Event.ON_DESTROY)
				navBackStackEntry.lifecycle.removeObserver(observer)
		})

		viewModel.shot.observe(viewLifecycleOwner) { shot ->
			closet_time_line_item.shot(shot)
			closet_time_line_item.onPersonNameClickListener {
				ViewShotFragmentDirections
					.actionToClosetFragment(null, shot.person.id)
					.navigate()
			}
			closet_time_line_item.onLikeButtonClickListener { v ->
				viewLifecycleOwner.lifecycleScope.launch {
					v.isEnabled = false
					try {
						when {
							shot.isViewerLike -> shotRepository.deleteLike(shot.id)
							else -> shotRepository.putLike(shot.id)
						}
					} catch (e: Exception) {
						showError(e)
					} finally {
						v.isEnabled = true
					}
				}
			}
			when (shot.person.id) {
				SessionPref.id -> {
					closet_time_line_item.setMenuButtonVisibility(View.VISIBLE)
					closet_time_line_item.onMenuButtonClickListener {
						PopupMenus.showShotOwner(this, it, shot, shotRepository)
					}
					closet_time_line_item.setBookmarkButtonVisibility(View.GONE)
					closet_time_line_item.onBookmarkButtonClickListener(null)
				}
				else -> {
					closet_time_line_item.setMenuButtonVisibility(View.GONE)
					closet_time_line_item.onMenuButtonClickListener(null)
					closet_time_line_item.setBookmarkButtonVisibility(View.VISIBLE)
					closet_time_line_item.onBookmarkButtonClickListener { v ->
						viewLifecycleOwner.lifecycleScope.launch {
							v.isEnabled = false
							try {
								when {
									shot.isViewerBookmark -> shotRepository.deleteBookmark(shot.id)
									else -> shotRepository.putBookmark(shot.id)
								}
							} catch (e: Exception) {
								showError(e)
							} finally {
								v.isEnabled = true
							}
						}
					}
				}
			}
		}
		viewModel.comments.observe(viewLifecycleOwner) {
			commentController.submitList(it)
		}
		viewModel.shotState.observe(viewLifecycleOwner) {
			if (it is NetworkState.Fail)
				alertErrorAndNavigateUp(it.error).show()
		}
		viewModel.commentsNetworkState.observe(viewLifecycleOwner) {
			commentController.networkState = it
		}
		viewModel.refreshState.observe(viewLifecycleOwner) {
			swipe_refresh_comment.isRefreshing = it is NetworkState.Fetching
		}
	}
}
