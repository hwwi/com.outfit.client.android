package com.outfit.client.android.ui.deletecomment

import androidx.navigation.fragment.navArgs
import com.outfit.client.android.R
import com.outfit.client.android.repository.CommentRepository
import com.outfit.client.android.widget.AbstractConfirmDialog
import javax.inject.Inject

class DeleteCommentConfirmDialog @Inject constructor(
	private val commentRepository: CommentRepository
) : AbstractConfirmDialog(
	R.string.dialog_title_delete_comment,
	R.string.dialog_content_delete_comment,
	R.string.msg_deleting,
	R.string.str_delete
) {
	private val args: DeleteCommentConfirmDialogArgs by navArgs()

	override suspend fun onCreateCompletable() {
		commentRepository.delete(args.shotId, args.commentId)
	}

}