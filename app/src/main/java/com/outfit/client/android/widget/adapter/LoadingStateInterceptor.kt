package com.outfit.client.android.widget.adapter

import com.airbnb.epoxy.*
import com.airbnb.epoxy.paging.PagedListEpoxyController
import com.outfit.client.android.R
import com.outfit.client.android.data.NetworkState
import com.outfit.client.android.error.ProblemDetailsException
import com.outfit.client.android.widget.item.ProgressItemModel_
import com.outfit.client.android.widget.item.RetryItemModel_

class LoadingStateInterceptor(
	private val controller: EpoxyController,
	private val onRetry: (() -> Unit)? = null
) : EpoxyController.Interceptor {

	init {
		when (controller) {
			is Typed2EpoxyController<*, *>,
			is Typed3EpoxyController<*, *, *>,
			is Typed4EpoxyController<*, *, *, *> ->
				throw UnsupportedOperationException("Typed2~4EpoxyController cannot trigger requestModelBuild")
		}
		controller.addInterceptor(this)
	}

	var loadingState: NetworkState<Any>? = null
		set(value) {
			val exValue = field
			if (exValue != null && value != null
				&& exValue::class == value::class
				&& value::class != NetworkState.Fail::class
			)
				return
			field = value
			when (controller) {
				is TypedEpoxyController<*> ->
					(controller as TypedEpoxyController<Any?>).setData(controller.currentData)
				else -> controller.requestDelayedModelBuild(0)
			}
		}

	override fun intercept(models: MutableList<EpoxyModel<*>>) {
		val loadingState = loadingState
		if (loadingState == null || loadingState is NetworkState.Success)
			return
		when (loadingState) {
			is NetworkState.Fetching -> models.add(ProgressItemModel_().apply {
				id("footer-progress")
			})
			is NetworkState.Fail -> models.add(RetryItemModel_().apply {
				id("footer-retry")
				when (val error = loadingState.error) {
					is ProblemDetailsException -> message(error.detail)
					else -> message(R.string.msg_error_occurred_please_try_again_later)
				}
				onRetryButtonClickListener { _ ->
					onRetry?.invoke()
				}
			})
		}
	}
}