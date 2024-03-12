package com.outfit.client.android.repository

import android.webkit.MimeTypeMap
import com.outfit.client.android.data.NetworkState
import com.outfit.client.android.data.args.*
import com.outfit.client.android.db.dao.PersonDetailDao
import com.outfit.client.android.extension.ignoreData
import com.outfit.client.android.extension.onSuccess
import com.outfit.client.android.network.api.AccountApi
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class AccountRepository @Inject constructor(
	private val accountApi: AccountApi,
	private val personDetailDao: PersonDetailDao
) {

	suspend fun logout(currentToken: String) =
		accountApi.putLogout(AccountPutLogoutArgs(currentToken))

	fun patchPassword(args: AccountPasswordPatchArgs): Flow<NetworkState<Unit>> =
		accountApi.patchPassword(args)

	fun resetPassword(args: AccountResetPasswordPatchArgs): Flow<NetworkState<Unit>> =
		accountApi.patchResetPassword(args)

	fun update(biography: String?): Flow<NetworkState<Unit>> =
		accountApi.put(AccountPutArgs(biography))
			.onSuccess {
				personDetailDao.updateBiography(
					it.personId,
					it.biography
				)
			}
			.ignoreData()

	fun patchName(args: AccountPatchNameArgs): Flow<NetworkState<Unit>> =
		accountApi.patchName(args)
			.onSuccess {
				personDetailDao.updateName(
					it.personId,
					it.name
				)
			}
			.ignoreData()

	private fun createFilePart(file: File): MultipartBody.Part =
		MultipartBody.Part.createFormData(
			"file",
			file.name,
			file.asRequestBody(
				MimeTypeMap.getSingleton()
					.getMimeTypeFromExtension(file.extension)
					?.toMediaTypeOrNull()
			)
		)

	suspend fun changeProfileImage(file: File) {
		val payload = accountApi.patchProfileImage(createFilePart(file))
		personDetailDao.setProfileImage(payload.personId, payload.profileImageUrl)
	}

	suspend fun changeClosetBackgroundImage(file: File) {
		val payload = accountApi.patchClosetBackgroundImage(createFilePart(file))
		personDetailDao.setClosetBackgroundImage(payload.personId, payload.closetBackgroundImageUrl)
	}

	suspend fun deleteProfileImage() {
		val payload = accountApi.deleteProfileImage()
		personDetailDao.setProfileImage(payload.personId, null)
	}

	suspend fun deleteClosetBackgroundImage() {
		val payload = accountApi.deleteClosetBackgroundImage()
		personDetailDao.setClosetBackgroundImage(payload.personId, null)
	}
}