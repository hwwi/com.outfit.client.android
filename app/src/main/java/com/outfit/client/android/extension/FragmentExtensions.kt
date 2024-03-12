package com.outfit.client.android.extension

import android.app.Dialog
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.outfit.client.android.R

fun Fragment.toast(@StringRes resId: Int): Toast = requireContext().toast(resId)
fun Fragment.toast(text: CharSequence): Toast = requireContext().toast(text)

fun Fragment.longToast(@StringRes resId: Int): Toast = requireContext().longToast(resId)
fun Fragment.longToast(text: CharSequence): Toast = requireContext().toast(text)


fun Fragment.getString(throwable: Throwable): String = requireActivity().getString(throwable)

fun Fragment.showError(
	throwable: Throwable,
	mapErrorToView: ((String) -> TextInputLayout?)? = null
): Toast = requireActivity().showError(throwable, mapErrorToView)

fun Fragment.alertErrorAndNavigateUp(
	throwable: Throwable
): Dialog = MaterialAlertDialogBuilder(requireContext())
	.apply {
		setCancelable(false)
		setMessage(getString(throwable))
		setPositiveButton(R.string.str_ok) { _, _ ->
			findNavController().navigateUp()
		}
	}
	.create()
