package com.outfit.client.android.di.module

import androidx.fragment.app.Fragment
import com.outfit.client.android.di.key.FragmentKey
import com.outfit.client.android.ui.anonymousverification.request.RequestCodeFragment
import com.outfit.client.android.ui.anonymousverification.verify.VerifyCodeFragment
import com.outfit.client.android.ui.authentication.resetpassword.ResetPasswordFragment
import com.outfit.client.android.ui.authentication.signin.SignInFragment
import com.outfit.client.android.ui.authentication.signup.SignUpFragment
import com.outfit.client.android.ui.bookmarks.BookmarksFragment
import com.outfit.client.android.ui.changepassword.ChangePasswordFragment
import com.outfit.client.android.ui.changeviewerclosetbackgroundimage.ChangeViewerClosetBackgroundImageDialog
import com.outfit.client.android.ui.changeviewerprofileimage.ChangeViewerProfileImageDialog
import com.outfit.client.android.ui.closet.ClosetFragment
import com.outfit.client.android.ui.deletecomment.DeleteCommentConfirmDialog
import com.outfit.client.android.ui.deletenotificationconfirm.DeleteNotificationConfirmDialog
import com.outfit.client.android.ui.deleteshot.DeleteShotConfirmDialog
import com.outfit.client.android.ui.deleteviewerclosetbackgroundimage.DeleteViewerClosetBackgroundImageDialog
import com.outfit.client.android.ui.deleteviewerprofileimage.DeleteViewerProfileImageDialog
import com.outfit.client.android.ui.editprofile.EditProfileFragment
import com.outfit.client.android.ui.explore.ExploreFragment
import com.outfit.client.android.ui.myaccount.MyAccountFragment
import com.outfit.client.android.ui.notifications.NotificationsFragment
import com.outfit.client.android.ui.post.confirm.ConfirmFragment
import com.outfit.client.android.ui.post.modifyitemtag.ModifyItemTagDialog
import com.outfit.client.android.ui.post.tagging.TaggingFragment
import com.outfit.client.android.ui.postcomment.PostCommentDialog
import com.outfit.client.android.ui.search.SearchFragment
import com.outfit.client.android.ui.selectimage.editimage.EditImageFragment
import com.outfit.client.android.ui.selectimage.selectimage.SelectImageFragment
import com.outfit.client.android.ui.subscriptions.SubscriptionsFragment
import com.outfit.client.android.ui.viewcomments.ViewCommentsFragment
import com.outfit.client.android.ui.viewfollow.ViewFollowFragment
import com.outfit.client.android.ui.viewfollow.ViewFollowerSubFragment
import com.outfit.client.android.ui.viewfollow.ViewFollowingSubFragment
import com.outfit.client.android.ui.viewhashtag.ViewHashTagFragment
import com.outfit.client.android.ui.viewitemtag.ViewItemTagFragment
import com.outfit.client.android.ui.viewshot.ViewShotFragment
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface FragmentBindModule {

	@Binds
	@IntoMap
	@FragmentKey(ConfirmFragment::class)
	fun bindConfirmFragment(fragment: ConfirmFragment): Fragment

	@Binds
	@IntoMap
	@FragmentKey(ClosetFragment::class)
	fun bindClosetFragment(fragment: ClosetFragment): Fragment

	@Binds
	@IntoMap
	@FragmentKey(ChangePasswordFragment::class)
	fun bindChangePasswordFragment(fragment: ChangePasswordFragment): Fragment

	@Binds
	@IntoMap
	@FragmentKey(DeleteCommentConfirmDialog::class)
	fun bindDeleteCommentConfirmDialog(fragment: DeleteCommentConfirmDialog): Fragment

	@Binds
	@IntoMap
	@FragmentKey(DeleteShotConfirmDialog::class)
	fun bindDeleteShotConfirmDialog(fragment: DeleteShotConfirmDialog): Fragment

	@Binds
	@IntoMap
	@FragmentKey(DeleteNotificationConfirmDialog::class)
	fun bindDeleteNotificationConfirmDialog(fragment: DeleteNotificationConfirmDialog): Fragment

	@Binds
	@IntoMap
	@FragmentKey(DeleteViewerProfileImageDialog::class)
	fun bindDeleteViewerProfilePhotoProgressDialog(fragment: DeleteViewerProfileImageDialog): Fragment

	@Binds
	@IntoMap
	@FragmentKey(EditProfileFragment::class)
	fun bindEditProfileFragment(fragment: EditProfileFragment): Fragment

	@Binds
	@IntoMap
	@FragmentKey(ExploreFragment::class)
	fun bindExploreFragment(fragment: ExploreFragment): Fragment

	@Binds
	@IntoMap
	@FragmentKey(MyAccountFragment::class)
	fun bindMyAccountFragment(fragment: MyAccountFragment): Fragment

	@Binds
	@IntoMap
	@FragmentKey(SelectImageFragment::class)
	fun bindSelectImageFragment(fragment: SelectImageFragment): Fragment

	@Binds
	@IntoMap
	@FragmentKey(EditImageFragment::class)
	fun bindEditImageFragment(fragment: EditImageFragment): Fragment

	@Binds
	@IntoMap
	@FragmentKey(SignInFragment::class)
	fun bindSignInFragment(fragment: SignInFragment): Fragment

	@Binds
	@IntoMap
	@FragmentKey(ResetPasswordFragment::class)
	fun bindResetPasswordFragment(fragment: ResetPasswordFragment): Fragment

	@Binds
	@IntoMap
	@FragmentKey(SignUpFragment::class)
	fun bindSignUpFragment(fragment: SignUpFragment): Fragment

	@Binds
	@IntoMap
	@FragmentKey(TaggingFragment::class)
	fun bindTaggingFragment(fragment: TaggingFragment): Fragment

	@Binds
	@IntoMap
	@FragmentKey(ViewShotFragment::class)
	fun bindViewShotFragment(fragment: ViewShotFragment): Fragment

	@Binds
	@IntoMap
	@FragmentKey(SubscriptionsFragment::class)
	fun bindSubscriptionsFragment(fragment: SubscriptionsFragment): Fragment

	@Binds
	@IntoMap
	@FragmentKey(BookmarksFragment::class)
	fun bindBookmarksFragment(fragment: BookmarksFragment): Fragment

	@Binds
	@IntoMap
	@FragmentKey(ViewCommentsFragment::class)
	fun bindViewCommentDialog(fragment: ViewCommentsFragment): Fragment

	@Binds
	@IntoMap
	@FragmentKey(SearchFragment::class)
	fun bindSearchFragment(fragment: SearchFragment): Fragment

	@Binds
	@IntoMap
	@FragmentKey(ViewItemTagFragment::class)
	fun bindViewItemTagFragment(fragment: ViewItemTagFragment): Fragment

	@Binds
	@IntoMap
	@FragmentKey(ViewHashTagFragment::class)
	fun bindViewHashTagFragment(fragment: ViewHashTagFragment): Fragment

	@Binds
	@IntoMap
	@FragmentKey(ViewFollowFragment::class)
	fun bindViewFollowFragment(fragment: ViewFollowFragment): Fragment

	@Binds
	@IntoMap
	@FragmentKey(ViewFollowerSubFragment::class)
	fun bindViewFollowerSubFragment(fragment: ViewFollowerSubFragment): Fragment

	@Binds
	@IntoMap
	@FragmentKey(ViewFollowingSubFragment::class)
	fun bindViewFollowingSubFragment(fragment: ViewFollowingSubFragment): Fragment

	@Binds
	@IntoMap
	@FragmentKey(ChangeViewerProfileImageDialog::class)
	fun bindChangeViewerProfilePhotoProgressDialog(fragment: ChangeViewerProfileImageDialog): Fragment

	@Binds
	@IntoMap
	@FragmentKey(RequestCodeFragment::class)
	fun bindAnonymousVerificationRequestCodeFragment(fragment: RequestCodeFragment): Fragment

	@Binds
	@IntoMap
	@FragmentKey(VerifyCodeFragment::class)
	fun bindAnonymousVerificationVerifyCodeFragment(fragment: VerifyCodeFragment): Fragment

	@Binds
	@IntoMap
	@FragmentKey(ModifyItemTagDialog::class)
	fun bindModifyItemTagDialog(fragment: ModifyItemTagDialog): Fragment

	@Binds
	@IntoMap
	@FragmentKey(NotificationsFragment::class)
	fun bindNotificationsFragment(fragment: NotificationsFragment): Fragment

	@Binds
	@IntoMap
	@FragmentKey(PostCommentDialog::class)
	fun bindPostCommentDialog(fragment: PostCommentDialog): Fragment

	@Binds
	@IntoMap
	@FragmentKey(ChangeViewerClosetBackgroundImageDialog::class)
	fun bindChangeViewerClosetBackgroundImageDialog(fragment: ChangeViewerClosetBackgroundImageDialog): Fragment

	@Binds
	@IntoMap
	@FragmentKey(DeleteViewerClosetBackgroundImageDialog::class)
	fun bindDeleteViewerClosetBackgroundImageDialog(fragment: DeleteViewerClosetBackgroundImageDialog): Fragment

}
