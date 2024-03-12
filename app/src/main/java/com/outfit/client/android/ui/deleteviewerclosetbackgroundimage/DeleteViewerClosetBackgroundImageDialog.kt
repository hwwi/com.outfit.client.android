package com.outfit.client.android.ui.deleteviewerclosetbackgroundimage

import com.outfit.client.android.repository.AccountRepository
import com.outfit.client.android.widget.AbstractProgressDialog
import javax.inject.Inject

class DeleteViewerClosetBackgroundImageDialog @Inject constructor(
	private val accountRepository: AccountRepository
) : AbstractProgressDialog() {

	override suspend fun onCreateCompletable() {
		accountRepository.deleteClosetBackgroundImage()
	}
}