package com.outfit.client.android.ui.deleteviewerprofileimage

import com.outfit.client.android.repository.AccountRepository
import com.outfit.client.android.widget.AbstractProgressDialog
import javax.inject.Inject

class DeleteViewerProfileImageDialog @Inject constructor(
	private val accountRepository: AccountRepository
) : AbstractProgressDialog() {

	override suspend fun onCreateCompletable() {
		accountRepository.deleteProfileImage()
	}
}