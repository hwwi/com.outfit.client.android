package com.outfit.client.android.widget.item

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.epoxy.CallbackProp
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.outfit.client.android.R
import com.outfit.client.android.data.NetworkState
import com.outfit.client.android.extension.getString
import kotlinx.android.synthetic.main.item_network_state.view.*

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class NetworkStateItem @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

	init {
		inflate(context, R.layout.item_network_state, this)
	}

	@JvmOverloads
	@CallbackProp
	fun onRetryButtonClickListener(onRetryButtonClickListener: OnClickListener? = null) {
		item_network_state_button_retry.setOnClickListener(onRetryButtonClickListener)
	}

	// https://github.com/airbnb/epoxy/issues/881
	@ModelProp(ModelProp.Option.IgnoreRequireHashCode)
	fun networkState(networkState: NetworkState<*>) {
		when (networkState) {
			is NetworkState.Fetching -> {
				item_network_state_progress.visibility = VISIBLE
				item_network_state_button_retry.visibility = INVISIBLE
				item_network_state_text_error.visibility = INVISIBLE
				item_network_state_text_error.text = null
			}
			is NetworkState.Fail -> {
				item_network_state_progress.visibility = INVISIBLE
				item_network_state_button_retry.visibility = VISIBLE
				item_network_state_text_error.visibility = VISIBLE
				item_network_state_text_error.text = context.getString(networkState.error)
			}
			is NetworkState.Success -> {
				item_network_state_progress.visibility = GONE
				item_network_state_button_retry.visibility = GONE
				item_network_state_text_error.visibility = GONE
				item_network_state_text_error.text = null
			}
		}
	}
}