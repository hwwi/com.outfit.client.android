package com.outfit.client.android.ui.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.activityViewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.airbnb.epoxy.EpoxyItemSpacingDecorator
import com.airbnb.epoxy.addGlidePreloader
import com.airbnb.epoxy.glidePreloader
import com.airbnb.epoxy.preload.ViewData
import com.airbnb.epoxy.preload.ViewMetadata
import com.bumptech.glide.RequestManager
import com.outfit.client.android.R
import com.outfit.client.android.data.NetworkState
import com.outfit.client.android.data.NotificationType
import com.outfit.client.android.databinding.FragmentNotificationsBinding
import com.outfit.client.android.pref.SessionPref
import com.outfit.client.android.ui.NavActivity
import com.outfit.client.android.widget.DataBindingFragment
import com.outfit.client.android.widget.item.NotificationItemModel_
import javax.inject.Inject

class NotificationsFragment @Inject constructor(
) : DataBindingFragment<FragmentNotificationsBinding>(R.layout.fragment_notifications) {
	private val viewModel: NotificationsViewModel by activityViewModels { viewModelProviderFactory }

	private val localBroadcastReceiver = object : BroadcastReceiver() {
		override fun onReceive(context: Context?, intent: Intent?) {
			if (intent?.action == NavActivity.ACTION_NAVIGATION_ITEM_RESELECTED_AND_BADGE_CLEARED
				&& intent.getIntExtra(NavActivity.KEY_ITEM_ID, 0) == R.id.notificationsFragment
			)
				viewModel.refresh()
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		val controller = NotificationController(
			onClickListener = { v, item ->
				when (item.type) {
					NotificationType.ShotPosted,
					NotificationType.ShotIncludePersonTag,
					NotificationType.ShotLiked ->
						item.shotId?.also { shotId ->
							NotificationsFragmentDirections
								.actionNotificationsFragmentToViewShotFragment(shotId, null)
								.navigate()
						}
					NotificationType.Commented,
					NotificationType.CommentIncludePersonTag,
					NotificationType.CommentLiked ->
						item.shotId?.also { shotId ->
							NotificationsFragmentDirections
								.actionNotificationsFragmentToViewCommentsFragment(
									shotId,
									item.commentId ?: 0L,
									true
								)
								.navigate()
						}
					NotificationType.Followed -> {
						val id = SessionPref.id
						NotificationsFragmentDirections
							.actionNotificationsFragmentToViewFollowFragment(id, false)
							.navigate()
					}
				}
			},
			onMenuButtonClickListener = { v, item ->
				PopupMenu(requireContext(), v)
					.apply {
						menu.add(R.string.msg_delete_this_notification).setOnMenuItemClickListener {
							NotificationsFragmentDirections
								.actionNotificationsFragmentToDeleteNotificationConfirmDialog(item.id)
								.navigate()
							true
						}
					}
					.show()
			},
			onProducerProfileClickListener = { v, item ->
				val personId = item.producer.id
				NotificationsFragmentDirections
					.actionNotificationsFragmentToClosetFragment(null, personId)
					.navigate()
			},
			onShotPreviewClickListener = { v, item ->
				item.shotId?.also { shotId ->
					NotificationsFragmentDirections
						.actionNotificationsFragmentToViewShotFragment(shotId, null)
						.navigate()

				}
			}
		)

		binding.listNotification.apply {
			setController(controller)
			setItemSpacingDp(8)
			addGlidePreloader(
				controller,
				requestManager,
				preloader = glidePreloader { requestManager: RequestManager, epoxyModel: NotificationItemModel_, viewData: ViewData<ViewMetadata?> ->
					requestManager.load(
						when (viewData.viewId) {
							R.id.item_notification_image_producer_profile -> epoxyModel.notification().producer.profileImageUrl
							R.id.item_notification_image_shot_preview -> epoxyModel.notification().shotPreviewImageUrl
							else -> null
						}
					)
				}
			)
		}

		viewModel.notifications.observe(viewLifecycleOwner) {
			controller.submitList(it)
			if (it.isNotEmpty()) {
				binding.error.clearError()
				return@observe
			}
			binding.error.setError(
				R.string.error_title_notification_is_empty,
				null
			)
		}

		viewModel.refreshState.observe(viewLifecycleOwner) {
			binding.swipeRefresh.isRefreshing = it is NetworkState.Fetching
		}

		binding.swipeRefresh.setOnRefreshListener {
			viewModel.refresh()
		}
//		if (!IntroducePref.isIntroduceNotificationFragment)
//			MaterialAlertDialogBuilder(requireContext())
//				.setMessage(R.string.introduce_notification_fragment)
//				.setPositiveButton(R.string.str_ok) { _, _ ->
//					IntroducePref.isIntroduceNotificationFragment = true
//				}
//				.show()
	}

	override fun onResume() {
		super.onResume()
		LocalBroadcastManager.getInstance(requireContext())
			.registerReceiver(
				localBroadcastReceiver,
				IntentFilter(NavActivity.ACTION_NAVIGATION_ITEM_RESELECTED_AND_BADGE_CLEARED)
			)
	}

	override fun onPause() {
		super.onPause()
		LocalBroadcastManager.getInstance(requireContext())
			.unregisterReceiver(localBroadcastReceiver)
	}
}