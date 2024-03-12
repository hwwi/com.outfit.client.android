package com.outfit.client.android.widget

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.outfit.client.android.R
import com.outfit.client.android.extension.getString
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class AbstractConfirmDialog(
	@StringRes private val titleRes: Int,
	@StringRes private val contentRes: Int,
	@StringRes private val fetchingContentRes: Int,
	@StringRes private val positiveButtonRes: Int,
	@StringRes private val negativeButtonRes: Int = R.string.str_cancel
) : DialogFragment() {

	abstract suspend fun onCreateCompletable()

	final override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
		val root = View.inflate(activity, R.layout.dialog_base, null)
		val content: TextView = root.findViewById<TextView>(R.id.content).apply {
			setText(contentRes)
		}
		val progressBar: ProgressBar = root.findViewById(R.id.progress)
		return AlertDialog.Builder(requireActivity())
			.setTitle(titleRes)
			.setView(root)
			.setPositiveButton(positiveButtonRes) { _, _ -> }
			.setNegativeButton(negativeButtonRes) { _, _ -> }
			.create()
			.apply {
				setCanceledOnTouchOutside(false)
				setOnShowListener(object : DialogInterface.OnShowListener {
					lateinit var positiveButton: Button
					lateinit var negativeButton: Button
					override fun onShow(dialog: DialogInterface?) {
						positiveButton = getButton(AlertDialog.BUTTON_POSITIVE)
						negativeButton = getButton(AlertDialog.BUTTON_NEGATIVE)
						positiveButton.setOnClickListener {
							lifecycleScope.launch {
								updateViewState(
									false,
									true,
									context.getString(fetchingContentRes)
								)

								try {
									onCreateCompletable()
								} catch (e: Throwable) {
									updateViewState(
										true, false,
										context.getString(e)
									)
									Timber.e(e)
								}
								onComplete()
								this@AbstractConfirmDialog.dismiss()
							}
						}
					}

					fun updateViewState(
						isButtonEnabled: Boolean,
						progressVisible: Boolean,
						errorStr: String
					) {
						progressBar.visibility = when {
							progressVisible -> View.VISIBLE
							else -> View.INVISIBLE
						}
						positiveButton.isEnabled = isButtonEnabled
						negativeButton.isEnabled = isButtonEnabled
						content.text = errorStr
					}
				})
			}
	}

	protected open fun onComplete() {}
}