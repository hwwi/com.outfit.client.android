package com.outfit.client.android.ui.deletenotificationconfirm

import androidx.navigation.fragment.navArgs
import com.outfit.client.android.R
import com.outfit.client.android.repository.NotificationRepository
import com.outfit.client.android.widget.AbstractConfirmDialog
import javax.inject.Inject

class DeleteNotificationConfirmDialog @Inject constructor(
	private val notificationRepository: NotificationRepository
) : AbstractConfirmDialog(
	R.string.dialog_title_delete_notification,
	R.string.dialog_content_delete_notification,
	R.string.msg_deleting,
	R.string.str_delete
) {
	private val args: DeleteNotificationConfirmDialogArgs by navArgs()

	override suspend fun onCreateCompletable() {
		notificationRepository.delete(args.notificationId)
	}
}