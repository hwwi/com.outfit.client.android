package com.outfit.client.android.binding

import android.view.View
import androidx.databinding.BindingAdapter

object BindingAdapters {

	@JvmStatic
	@BindingAdapter("visibleOrGone")
	fun visibleOrGone(view: View, show: Boolean) {
		view.visibility = if (show) View.VISIBLE else View.GONE
	}

	@JvmStatic
	@BindingAdapter("goneOrVisible")
	fun goneOrVisible(view: View, show: Boolean) {
		view.visibility = if (show) View.GONE else View.VISIBLE
	}

	@JvmStatic
	@BindingAdapter("enabledWhen")
	fun enabledWhen(view: View, enabled: Boolean) {
		view.isEnabled = enabled
	}

	@JvmStatic
	@BindingAdapter("disabledWhen")
	fun disabledWhen(view: View, disabled: Boolean) {
		view.isEnabled = !disabled
	}


//	@JvmStatic
//	@BindingAdapter("enabledWhen")
//	fun clearErrorWhen(view: View, enabled: Boolean) {
//		view.error = enabled
//	}

}