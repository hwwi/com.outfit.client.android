package com.outfit.client.android.ui.postcomment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.text.HtmlCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.outfit.client.android.R
import com.outfit.client.android.extension.showError
import com.outfit.client.android.repository.CommentRepository
import kotlinx.android.synthetic.main.fragment_post_comment.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class PostCommentDialog @Inject constructor(
	private val commentRepository: CommentRepository
) : BottomSheetDialogFragment() {

	private val args: PostCommentDialogArgs by navArgs()

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? = inflater.inflate(R.layout.fragment_post_comment, null, false)


	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		post_comment_button_post.setOnClickListener {
			viewLifecycleOwner.lifecycleScope.launch {
				isCancelable = false
				post_comment_button_post.isEnabled = false
				post_comment_edit_comment_text.isEnabled = false
				post_comment_progress.visibility = View.VISIBLE
				try {
					commentRepository.postNewComment(
						args.shotId,
						args.replyToComment,
						post_comment_edit_comment_text.text?.toString() ?: ""
					)
					dismiss()
				} catch (e: Exception) {
					Timber.e(e)
					showError(e)
				} finally {
					isCancelable = true
					post_comment_button_post.isEnabled = true
					post_comment_edit_comment_text.isEnabled = true
					post_comment_progress.visibility = View.GONE
				}
			}
		}
		when (val comment = args.replyToComment) {
			null -> post_comment_text_reply_to.visibility = View.GONE
			else -> {
				post_comment_text_reply_to.text = HtmlCompat.fromHtml(
					getString(R.string.msg_reply_to, comment.person.name),
					HtmlCompat.FROM_HTML_MODE_LEGACY
				)
				val text = getString(R.string.tag_person, comment.person.name) + " "
				post_comment_edit_comment_text.setText(text)
				post_comment_edit_comment_text.setSelection(text.length)
			}
		}
		post_comment_edit_comment_text.requestFocus()
		dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
	}
}