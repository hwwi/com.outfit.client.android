package com.outfit.client.android.widget

import android.app.Dialog
import android.os.Bundle
import android.widget.ProgressBar
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.outfit.client.android.R
import com.outfit.client.android.extension.showError
import com.outfit.client.android.extension.toast
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class AbstractProgressDialog(
	@StringRes private val onActionToastRes: Int = R.string.msg_has_been_applied
) : DialogFragment() {

	abstract suspend fun onCreateCompletable()

	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
		return AlertDialog.Builder(
			requireActivity(),
			R.style.ThemeOverlay_MaterialComponents_Dialog_Alert_Transparent
		)
			.setView(
				ProgressBar(context, null, android.R.attr.progressBarStyleLargeInverse).apply {
					isIndeterminate = true
				}
			)
			.setCancelable(false)
			.create()
			.apply {
				setOnShowListener {
					lifecycleScope.launch {
						try {
							onCreateCompletable()
						} catch (e: Throwable) {
							showError(e)
							Timber.e(e)
							dismiss()
							return@launch
						}
						toast(onActionToastRes)
						dismiss()
					}
				}
			}
	}

	fun NavDirections.navigate(navOptions: NavOptions? = null) {
		findNavController().navigate(this, navOptions)
	}
}