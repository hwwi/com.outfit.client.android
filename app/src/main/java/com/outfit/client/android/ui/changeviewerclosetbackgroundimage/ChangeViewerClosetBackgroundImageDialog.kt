package com.outfit.client.android.ui.changeviewerclosetbackgroundimage

import androidx.navigation.fragment.navArgs
import com.outfit.client.android.repository.AccountRepository
import com.outfit.client.android.widget.AbstractProgressDialog
import java.io.File
import javax.inject.Inject

class ChangeViewerClosetBackgroundImageDialog @Inject constructor(
	private val accountRepository: AccountRepository
) : AbstractProgressDialog() {
	private val args: ChangeViewerClosetBackgroundImageDialogArgs by navArgs()

	override suspend fun onCreateCompletable() {
		accountRepository.changeClosetBackgroundImage(File(args.imageFilePath))
	}
}