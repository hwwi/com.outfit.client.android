package com.outfit.client.android.ui.viewcomments

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.outfit.client.android.R
import com.outfit.client.android.data.NetworkState
import com.outfit.client.android.extension.alertErrorAndNavigateUp
import com.outfit.client.android.extension.showError
import com.outfit.client.android.glide.GlideApp
import com.outfit.client.android.pref.SessionPref
import com.outfit.client.android.repository.CommentRepository
import com.outfit.client.android.text.style.OnTagSpansClickListener
import com.outfit.client.android.util.PopupMenus
import com.outfit.client.android.util.autoCleared
import com.outfit.client.android.widget.SyntheticFragment
import com.outfit.client.android.widget.controller.CommentPagedController
import kotlinx.android.synthetic.main.fragment_view_comment.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class ViewCommentsFragment @Inject constructor(
	private val commentRepository: CommentRepository
) : SyntheticFragment(R.layout.fragment_view_comment) {
	private val viewModel: ViewCommentsViewModel by viewModels { viewModelProviderFactory }
	private val args: ViewCommentsFragmentArgs by navArgs()

	private var commentController: CommentPagedController by autoCleared()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		view_comments_image_shot_preview.setOnClickListener {
			ViewCommentsFragmentDirections.actionViewCommentDialogToViewShotFragment(
				args.shotId,
				null
			)
				.navigate()
		}
		view_comments_button_post_comment.setOnClickListener {
			ViewCommentsFragmentDirections.actionViewCommentDialogToPostCommentDialog(
				args.shotId,
				null
			)
				.navigate()
		}
		commentController = CommentPagedController(
			onCommentReplyClickListener = { comment ->
				ViewCommentsFragmentDirections.actionViewCommentDialogToPostCommentDialog(
					args.shotId,
					comment
				)
					.navigate()
			},
			onCommentMenuClickListener = { v, comment ->
				when (comment.person.id) {
					SessionPref.id -> PopupMenus.showCommentOwner(
						this@ViewCommentsFragment,
						v,
						comment
					)
//					else -> PopupMenus.showComment(this@ViewCommentsFragment, v, comment)
				}
			},
			onCommentLikeClickListener = { v, comment ->
				viewLifecycleOwner.lifecycleScope.launch {
					v.isEnabled = false
					try {
						when {
							comment.isViewerLike -> commentRepository.deleteLike(
								comment.shotId,
								comment.id
							)
							else -> commentRepository.putLike(comment.shotId, comment.id)
						}
					} catch (e: Exception) {
						showError(e)
					} finally {
						v.isEnabled = true
					}
				}
			},
			onRetryButtonClickListener = {
				viewModel.retryComments()
			},
			onTagSpansClickListener = object : OnTagSpansClickListener {
				override fun onPersonTagClick(name: String) {
					ViewCommentsFragmentDirections.actionViewCommentDialogToClosetFragment(name)
						.navigate()
				}

				override fun onItemTagClick(brand: String, product: String?) {
					ViewCommentsFragmentDirections.actionViewCommentDialogToViewItemTagFragment(
						brand,
						product
					)
						.navigate()
				}

				override fun onHashTagClick(tag: String) {
					ViewCommentsFragmentDirections.actionViewCommentDialogToViewHashTagFragment(tag)
						.navigate()
				}
			}
		).apply {
			highlightCommentId = when (val commentId = args.nullableScrollToCommentId) {
				0L -> null
				else -> commentId
			}
		}
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
			view_comments_list_comment.isNestedScrollingEnabled = false
		}
		view_comments_list_comment.setItemSpacingDp(8)
		view_comments_list_comment.setController(commentController)

		view_comments_swipe_refresh_comment.setOnRefreshListener {
			viewModel.refreshComments()
		}

		viewModel.shot.observe(viewLifecycleOwner) {
			view_comments_flow_header.visibility = View.VISIBLE
			view_comments_text_comments_on_s_shot.text =
				getString(R.string.msg_comments_on_s_shot, it.person.name)
			GlideApp.with(this)
				.load(it.images.first().url)
				.error(R.drawable.ic_error_outline_24dp)
				.into(view_comments_image_shot_preview)
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
		viewModel.commentsRefreshState.observe(viewLifecycleOwner) {
			view_comments_swipe_refresh_comment.isRefreshing = it is NetworkState.Fetching
		}

	}
}
