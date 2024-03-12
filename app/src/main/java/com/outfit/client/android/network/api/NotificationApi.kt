package com.outfit.client.android.network.api

import com.outfit.client.android.data.NetworkState
import com.outfit.client.android.data.args.Direction
import com.outfit.client.android.data.args.NotificationPostCloudMessagingTokenArgs
import com.outfit.client.android.data.args.SortOrder
import com.outfit.client.android.data.dto.Connection
import com.outfit.client.android.data.dto.NotificationDto
import kotlinx.coroutines.flow.Flow
import retrofit2.http.*

interface NotificationApi {
	@POST("notification/cloudMessagingToken")
	suspend fun postCloudMessagingToken(
		@Body body: NotificationPostCloudMessagingTokenArgs
	)

	@DELETE("notification/{notificationId}")
	suspend fun deleteNotification(
		@Path("notificationId") notificationId: Long
	)

	@GET("notification")
	fun getConnection(
		@Query("cursor") cursor: Long?,
		@Query("direction") direction: Direction,
		@Query("sortOrder") sortOrder: SortOrder,
		@Query("limit") limit: Int
	): Flow<NetworkState<Connection<NotificationDto>>>
}