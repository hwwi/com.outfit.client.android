package com.outfit.client.android.extension

import android.content.Context
import android.util.TypedValue
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import com.google.android.material.textfield.TextInputLayout
import com.outfit.client.android.R
import com.outfit.client.android.error.ProblemDetailsException
import com.outfit.client.android.error.ResponseStatusException
import com.outfit.client.android.error.ValidationProblemDetailsException

@ColorInt
fun Context.getColorFromAttr(
	@AttrRes attrColor: Int,
	typedValue: TypedValue = TypedValue(),
	resolveRefs: Boolean = true
): Int {
	theme.resolveAttribute(attrColor, typedValue, resolveRefs)
	return typedValue.data
}

fun Context.toast(@StringRes resId: Int): Toast = toast(getString(resId))
fun Context.toast(text: CharSequence): Toast =
	Toast.makeText(this, text, Toast.LENGTH_SHORT).apply { show() }

fun Context.longToast(@StringRes resId: Int): Toast = longToast(getString(resId))
fun Context.longToast(text: CharSequence): Toast =
	Toast.makeText(this, text, Toast.LENGTH_LONG).apply { show() }


fun Context.getString(throwable: Throwable): String = when (throwable) {
	is ResponseStatusException -> getString(
		when (throwable.status) {
			401 -> R.string.msg_your_session_has_expired_please_login_again
			else -> R.string.msg_error_occurred_please_try_again_later
		}
	)
	is ProblemDetailsException -> throwable.detail
	else -> {
		getString(R.string.msg_error_occurred_please_try_again_later)
	}
}

fun Context.showError(
	e: Throwable,
	mapErrorToView: ((String) -> TextInputLayout?)? = null
): Toast = when {
	e is ValidationProblemDetailsException && mapErrorToView != null -> {
		val errorCodePerView =
			e.errors.keys.map { errorCode -> errorCode to mapErrorToView(errorCode) }
		val notMappedErrorField: List<String> = errorCodePerView.mapNotNull {
			val errorField = it.first
			when (val view = it.second) {
				null -> errorField
				else -> {
					view.error = e.errors[errorField]?.joinToString()
					null
				}
			}
		}
		when (notMappedErrorField.isNotEmpty()) {
			true -> longToast(
				buildString {
					appendLine(getString(e))
					e.errors
						.filter { notMappedErrorField.contains(it.key) }
						.flatMap { entry -> entry.value.map { "${entry.key} : $it" } }
						.joinTo(buffer = this, separator = "\n")
				}
			)
			else -> toast(getString(e))
		}
	}
	else -> toast(getString(e))
}