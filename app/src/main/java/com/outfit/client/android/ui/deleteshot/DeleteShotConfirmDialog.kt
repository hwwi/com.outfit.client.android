package com.outfit.client.android.ui.deleteshot

import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.outfit.client.android.R
import com.outfit.client.android.repository.ShotRepository
import com.outfit.client.android.widget.AbstractConfirmDialog
import javax.inject.Inject

class DeleteShotConfirmDialog @Inject constructor(
	private val shotRepository: ShotRepository
) : AbstractConfirmDialog(
	R.string.dialog_title_delete_shot,
	R.string.dialog_content_delete_shot,
	R.string.msg_deleting,
	R.string.str_delete
) {

	companion object {
		const val KEY_IS_DELETED = "KEY_IS_DELETED"
	}

	private val args: DeleteShotConfirmDialogArgs by navArgs()

	override suspend fun onCreateCompletable() {
		shotRepository.delete(args.shotId)
	}

	override fun onComplete() {
		findNavController().previousBackStackEntry
			?.savedStateHandle
			?.set(KEY_IS_DELETED, true)
	}
}