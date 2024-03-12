package com.outfit.client.android.repository

import androidx.paging.DataSource
import com.outfit.client.android.data.NetworkState
import com.outfit.client.android.data.args.ConnectionArgs
import com.outfit.client.android.data.model.Notification
import com.outfit.client.android.db.dao.NotificationDao
import com.outfit.client.android.extension.ignoreData
import com.outfit.client.android.extension.onSuccess
import com.outfit.client.android.network.api.NotificationApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NotificationRepository @Inject constructor(
	private val notificationDao: NotificationDao,
	private val notificationApi: NotificationApi
) {

	fun findAll(): DataSource.Factory<Int, Notification> = notificationDao.findAll()

	fun fetchConnection(
		connectionArgs: ConnectionArgs,
		isRefresh: Boolean = false
	): Flow<NetworkState<Unit>> =
		notificationApi
			.getConnection(
				connectionArgs.cursor,
				connectionArgs.direction,
				connectionArgs.sortOrder,
				connectionArgs.limit
			)
			.onSuccess {
				when (isRefresh) {
					true -> notificationDao.refresh(it.edges)
					false -> notificationDao.insertOrReplace(it.edges)
				}
			}
			.ignoreData()


	suspend fun delete(notificationId: Long) {
		notificationApi.deleteNotification(notificationId)
		notificationDao.delete(notificationId)
	}

}
