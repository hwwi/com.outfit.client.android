package com.outfit.client.android.ui.closet

import android.animation.ArgbEvaluator
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.core.view.animation.PathInterpolatorCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.airbnb.epoxy.addGlidePreloader
import com.airbnb.epoxy.glidePreloader
import com.airbnb.epoxy.preload.ViewData
import com.airbnb.epoxy.preload.ViewMetadata
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import com.outfit.client.android.R
import com.outfit.client.android.data.CacheType
import com.outfit.client.android.data.NetworkState
import com.outfit.client.android.databinding.FragmentClosetBinding
import com.outfit.client.android.extension.alertErrorAndNavigateUp
import com.outfit.client.android.extension.asHtml
import com.outfit.client.android.extension.showError
import com.outfit.client.android.glide.GlideApp
import com.outfit.client.android.pref.SessionPref
import com.outfit.client.android.repository.PersonRepository
import com.outfit.client.android.util.autoCleared
import com.outfit.client.android.widget.DataBindingFragment
import com.outfit.client.android.widget.adapter.LoadingStateInterceptor
import com.outfit.client.android.widget.controller.ShotThumbnailPagedController
import com.outfit.client.android.widget.item.ThumbnailItemModel_
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs


class ClosetFragment @Inject constructor(
	private val personRepository: PersonRepository
) : DataBindingFragment<FragmentClosetBinding>(R.layout.fragment_closet) {
	private val viewModel: ClosetViewModel by viewModels { viewModelProviderFactory }

	private var controller: ShotThumbnailPagedController by autoCleared()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		val typedValue = TypedValue()
		view.context.theme.resolveAttribute(R.attr.colorOnBackground, typedValue, true)
		val toolbarTitleColor = typedValue.data

		binding.textShotsCount.setOnClickListener {
			binding.layoutAppbar.setExpanded(false, true)
		}
		binding.layoutAppbar.addOnOffsetChangedListener(object :
			AppBarLayout.OnOffsetChangedListener {
			private val evaluator = ArgbEvaluator()
			private val interpolator = PathInterpolatorCompat.create(1f, 0f, 1f, 0f)

			override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
				val percentage = abs(verticalOffset.toFloat() / appBarLayout.totalScrollRange)
				val interpolation = interpolator.getInterpolation(percentage)
				val value =
					evaluator.evaluate(interpolation, Color.TRANSPARENT, toolbarTitleColor) as Int
				binding.toolbar.setTitleTextColor(value)
			}
		})
		controller = ShotThumbnailPagedController(
			onThumbnailClickListener = {
				ClosetFragmentDirections
					.actionClosetFragmentToViewShotFragment(it.id, CacheType.CLOSET)
					.navigate()
			}
		)
		val loadingStateInterceptor = LoadingStateInterceptor(controller) {
			viewModel.retry()
		}

		binding.shotList.apply {
			setController(controller)
			addGlidePreloader(
				controller,
				GlideApp.with(this@ClosetFragment),
				preloader = glidePreloader { requestManager: RequestManager, epoxyModel: ThumbnailItemModel_, _: ViewData<ViewMetadata?> ->
					requestManager.load(epoxyModel.imageUri())
				}
			)
		}
		binding.layoutTab.apply {
			addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
				override fun onTabSelected(tab: TabLayout.Tab?) {
					tab ?: return
					val isPrivate = tab.position != 0
					viewModel.setPrivate(isPrivate)
				}

				override fun onTabUnselected(tab: TabLayout.Tab?) {}

				override fun onTabReselected(tab: TabLayout.Tab?) {}
			})
			if (viewModel.isPrivate == true)
				getTabAt(1)?.let { tab ->
					selectTab(tab)
				}
		}

		viewModel.personId.observe(viewLifecycleOwner) { personId ->
			binding.layoutTab.visibility = when (personId) {
				SessionPref.id -> View.VISIBLE
				else -> View.GONE
			}
		}

		viewModel.personDetail.observe(viewLifecycleOwner) { personDetail ->
			binding.textFollowersCount.setOnClickListener {
				ClosetFragmentDirections
					.actionClosetFragmentToViewFollowFragment(
						personDetail.id,
						false
					)
					.navigate()
			}
			binding.textFollowingsCount.setOnClickListener {
				ClosetFragmentDirections
					.actionClosetFragmentToViewFollowFragment(
						personDetail.id,
						true
					)
					.navigate()
			}

			binding.toolbar.title = personDetail.name
			binding.textPersonName.text = personDetail.name
			binding.textPersonBiography.visibility = when {
				personDetail.biography.isBlank() -> View.GONE
				else -> View.VISIBLE
			}
			binding.textPersonBiography.text = personDetail.biography
			binding.textShotsCount.text =
				getString(R.string.str_d_shot, personDetail.shotsCount).asHtml()
			binding.textFollowersCount.text =
				getString(R.string.str_d_follower, personDetail.followersCount).asHtml()
			binding.textFollowingsCount.text =
				getString(R.string.str_d_following, personDetail.followingsCount).asHtml()

			when (personDetail.isViewerFollow) {
				true -> {
					binding.buttonToggleFollow.setText(R.string.str_unfollow)
					binding.buttonToggleFollow.setOnClickListener {
						viewLifecycleOwner.lifecycleScope.launch {
							try {
								personRepository.deleteFollowing(personDetail.id)
							} catch (e: Exception) {
								showError(e)
							}
						}
					}
				}
				false -> {
					binding.buttonToggleFollow.setText(R.string.str_follow)
					binding.buttonToggleFollow.setOnClickListener {
						viewLifecycleOwner.lifecycleScope.launch {
							try {
								personRepository.putFollowing(personDetail.id)
							} catch (e: Exception) {
								showError(e)
							}
						}
					}
				}
				null -> {
					binding.groupProfileButton.visibility = View.GONE
				}
			}

			when (val url = personDetail.closetBackgroundImageUrl) {
				null -> requestManager.clear(binding.imageClosetBackground)
				else -> requestManager
					.load(url)
					.into(binding.imageClosetBackground)
			}
			when (val url = personDetail.profileImageUrl) {
				null -> {
					requestManager.clear(binding.toolbar)
					requestManager.clear(binding.imagePersonProfile)
				}
				else -> {
					requestManager
						.load(url)
						.apply(
							RequestOptions.bitmapTransform(
								RoundedCorners(
									TypedValue.applyDimension(
										TypedValue.COMPLEX_UNIT_DIP,
										4f,
										resources.displayMetrics
									).toInt()
								)
							)
						)
						.fallback(R.drawable.ic_person_24dp)
						.error(R.drawable.ic_error_outline_24dp)
						.into(object : DrawableImageViewTarget(binding.imagePersonProfile) {
							override fun onResourceReady(
								resource: Drawable,
								transition: Transition<in Drawable>?
							) {
								super.onResourceReady(resource, transition)
								binding.toolbar.collapseIcon = resource
							}
						})
				}
			}
		}
		viewModel.personDetailState.observe(viewLifecycleOwner) {
			if (it is NetworkState.Fail) {
				alertErrorAndNavigateUp(it.error).show()
			}
		}

		viewModel.shots.observe(viewLifecycleOwner) {
			controller.submitList(it)

			if (it.isNotEmpty()) {
				binding.error.clearError()
				return@observe
			}

			when (viewModel.isPrivate) {
				true -> binding.error.setError(
					R.string.error_title_no_private_shots,
					R.string.error_msg_no_private_shots
				)
				false -> binding.error.setError(
					R.string.error_title_no_shots,
					R.string.error_msg_no_shots
				)
				null -> binding.error.clearError()
			}
		}

		viewModel.shotsState.observe(viewLifecycleOwner) {
			val state = it.getContentIfNotHandled() ?: return@observe
			loadingStateInterceptor.loadingState = state
		}

		viewModel.shotsRefreshState.observe(viewLifecycleOwner) {
			val state = it.getContentIfNotHandled() ?: return@observe
			binding.swipeRefresh.isRefreshing = state is NetworkState.Fetching
			when (state) {
				is NetworkState.Fetching -> loadingStateInterceptor.loadingState = null
				is NetworkState.Fail -> showError(state.error)
			}
		}

		binding.swipeRefresh.setOnRefreshListener {
			viewModel.refresh()
		}
	}
}