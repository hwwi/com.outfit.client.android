package com.outfit.client.android.di.module

import androidx.lifecycle.ViewModel
import com.outfit.client.android.di.*
import com.outfit.client.android.di.key.ViewModelKey
import com.outfit.client.android.ui.anonymousverification.request.RequestCodeViewModel
import com.outfit.client.android.ui.anonymousverification.verify.VerifyCodeViewModel
import com.outfit.client.android.ui.authentication.resetpassword.ResetPasswordViewModel
import com.outfit.client.android.ui.authentication.signin.SignInViewModel
import com.outfit.client.android.ui.authentication.signup.SignUpViewModel
import com.outfit.client.android.ui.bookmarks.BookmarksViewModel
import com.outfit.client.android.ui.changepassword.ChangePasswordViewModel
import com.outfit.client.android.ui.closet.ClosetViewModel
import com.outfit.client.android.ui.editprofile.EditProfileViewModel
import com.outfit.client.android.ui.explore.ExploreViewModel
import com.outfit.client.android.ui.notifications.NotificationsViewModel
import com.outfit.client.android.ui.post.confirm.ConfirmViewModel
import com.outfit.client.android.ui.post.modifyitemtag.ModifyItemTagViewModel
import com.outfit.client.android.ui.post.tagging.TaggingViewModel
import com.outfit.client.android.ui.search.SearchViewModel
import com.outfit.client.android.ui.selectimage.editimage.EditImageViewModel
import com.outfit.client.android.ui.selectimage.selectimage.SelectImageViewModel
import com.outfit.client.android.ui.subscriptions.SubscriptionsViewModel
import com.outfit.client.android.ui.viewcomments.ViewCommentsViewModel
import com.outfit.client.android.ui.viewfollow.ViewFollowViewModel
import com.outfit.client.android.ui.viewfollow.ViewFollowerSubViewModel
import com.outfit.client.android.ui.viewfollow.ViewFollowingSubViewModel
import com.outfit.client.android.ui.viewhashtag.ViewHashTagViewModel
import com.outfit.client.android.ui.viewitemtag.ViewItemTagViewModel
import com.outfit.client.android.ui.viewshot.ViewShotViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap


@Module
interface ViewModelBindModule {

	@Binds
	@IntoMap
	@ViewModelKey(ExploreViewModel::class)
	fun bindExploreViewModelFactory(viewModel: ExploreViewModel.Factory): AssistedSavedStateViewModelFactory

	@Binds
	@IntoMap
	@ViewModelKey(SubscriptionsViewModel::class)
	fun bindSubscriptionsViewModel(viewModel: SubscriptionsViewModel.Factory): AssistedSavedStateViewModelFactory

	@Binds
	@IntoMap
	@ViewModelKey(BookmarksViewModel::class)
	fun bindBookmarksViewModel(viewModel: BookmarksViewModel): ViewModel

	@Binds
	@IntoMap
	@ViewModelKey(SelectImageViewModel::class)
	fun bindSelectImageViewModel(viewModel: SelectImageViewModel.Factory): AssistedSavedStateArgsViewModelFactory<*>

	@Binds
	@IntoMap
	@ViewModelKey(EditImageViewModel::class)
	fun bindEditImageViewModel(viewModel: EditImageViewModel.Factory): AssistedArgsViewModelFactory<*>

	@Binds
	@IntoMap
	@ViewModelKey(SignUpViewModel::class)
	fun bindSignUpViewModel(viewModel: SignUpViewModel.Factory): AssistedCurrentBackStackEntryViewModelFactory

	@Binds
	@IntoMap
	@ViewModelKey(ViewShotViewModel::class)
	fun bindViewShotViewModel(viewModel: ViewShotViewModel.Factory): AssistedArgsViewModelFactory<*>

	@Binds
	@IntoMap
	@ViewModelKey(ViewCommentsViewModel::class)
	fun bindViewCommentViewModel(viewModel: ViewCommentsViewModel.Factory): AssistedArgsViewModelFactory<*>

	@Binds
	@IntoMap
	@ViewModelKey(VerifyCodeViewModel::class)
	fun bindAnonymousVerifyCodeViewModel(viewModel: VerifyCodeViewModel.Factory): AssistedArgsViewModelFactory<*>

	@Binds
	@IntoMap
	@ViewModelKey(RequestCodeViewModel::class)
	fun bindAnonymousRequestCodeViewModel(viewModel: RequestCodeViewModel.Factory): AssistedArgsViewModelFactory<*>

	@Binds
	@IntoMap
	@ViewModelKey(ConfirmViewModel::class)
	fun bindPostViewModel(viewModel: ConfirmViewModel.Factory): AssistedCurrentBackStackEntryViewModelFactory

	@Binds
	@IntoMap
	@ViewModelKey(ClosetViewModel::class)
	fun bindClosetViewModelFactory(factory: ClosetViewModel.Factory): AssistedSavedStateArgsViewModelFactory<*>

	@Binds
	@IntoMap
	@ViewModelKey(ViewItemTagViewModel::class)
	fun bindViewItemTagViewModel(factory: ViewItemTagViewModel.Factory): AssistedSavedStateArgsViewModelFactory<*>

	@Binds
	@IntoMap
	@ViewModelKey(ViewHashTagViewModel::class)
	fun bindViewHashTagViewModel(factory: ViewHashTagViewModel.Factory): AssistedSavedStateArgsViewModelFactory<*>

	@Binds
	@IntoMap
	@ViewModelKey(ViewFollowingSubViewModel::class)
	fun bindViewFollowingSubViewModel(factory: ViewFollowingSubViewModel): ViewModel

	@Binds
	@IntoMap
	@ViewModelKey(ViewFollowerSubViewModel::class)
	fun bindViewFollowerSubViewModel(factory: ViewFollowerSubViewModel): ViewModel

	@Binds
	@IntoMap
	@ViewModelKey(ViewFollowViewModel::class)
	fun bindViewViewFollowViewModel(viewModel: ViewFollowViewModel.Factory): AssistedArgsViewModelFactory<*>

	@Binds
	@IntoMap
	@ViewModelKey(SearchViewModel::class)
	fun bindSearchViewModel(viewModel: SearchViewModel): ViewModel

	@Binds
	@IntoMap
	@ViewModelKey(ChangePasswordViewModel::class)
	fun bindChangePasswordViewModel(viewModel: ChangePasswordViewModel): ViewModel

	@Binds
	@IntoMap
	@ViewModelKey(EditProfileViewModel::class)
	fun bindEditProfileViewModel(viewModel: EditProfileViewModel.Factory): AssistedCurrentBackStackEntryViewModelFactory


	@Binds
	@IntoMap
	@ViewModelKey(SignInViewModel::class)
	fun bindSignInViewModel(viewModel: SignInViewModel.Factory): AssistedSavedStateViewModelFactory

	@Binds
	@IntoMap
	@ViewModelKey(ResetPasswordViewModel::class)
	fun bindResetPasswordViewModel(viewModel: ResetPasswordViewModel.Factory): AssistedCurrentBackStackEntryViewModelFactory


	@Binds
	@IntoMap
	@ViewModelKey(TaggingViewModel::class)
	fun bindTaggingViewModel(viewModel: TaggingViewModel.Factory): AssistedPreviousBackStackEntryViewModelFactory

	@Binds
	@IntoMap
	@ViewModelKey(ModifyItemTagViewModel::class)
	fun bindModifyItemTagViewModel(viewModel: ModifyItemTagViewModel.Factory): AssistedArgsViewModelFactory<*>

	@Binds
	@IntoMap
	@ViewModelKey(NotificationsViewModel::class)
	fun bindNotificationsViewModel(viewModel: NotificationsViewModel): ViewModel
}