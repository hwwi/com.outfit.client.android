package com.outfit.client.android.ui.changeviewerprofileimage

import androidx.navigation.fragment.navArgs
import com.outfit.client.android.repository.AccountRepository
import com.outfit.client.android.widget.AbstractProgressDialog
import java.io.File
import javax.inject.Inject

class ChangeViewerProfileImageDialog @Inject constructor(
	private val accountRepository: AccountRepository
) : AbstractProgressDialog() {
	private val args: ChangeViewerProfileImageDialogArgs by navArgs()

	override suspend fun onCreateCompletable() {
		accountRepository.changeProfileImage(File(args.imageFilePath))
	}
}