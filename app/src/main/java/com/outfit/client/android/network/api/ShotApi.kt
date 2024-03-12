package com.outfit.client.android.network.api

import com.outfit.client.android.data.NetworkState
import com.outfit.client.android.data.args.Direction
import com.outfit.client.android.data.args.ShotPostArgs
import com.outfit.client.android.data.args.SortOrder
import com.outfit.client.android.data.dto.Connection
import com.outfit.client.android.data.dto.ShotDto
import com.outfit.client.android.data.payload.LikeShotPayload
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import retrofit2.http.*

interface ShotApi {
	@GET("shots")
	fun getConnection(
		@Query("cursor") cursor: Long?,
		@Query("direction") direction: Direction,
		@Query("sortOrder") sortOrder: SortOrder,
		@Query("limit") limit: Int
	): Flow<NetworkState<Connection<ShotDto>>>

	@GET("shots")
	suspend fun getConnectionByBrandProductTag(
		@Query("cursor") cursor: Long?,
		@Query("direction") direction: Direction,
		@Query("sortOrder") sortOrder: SortOrder,
		@Query("limit") limit: Int,
		@Query("brandCode") brand: String,
		@Query("productCode") product: String?
	): Connection<ShotDto>

	@GET("shots")
	suspend fun getConnectionByHashTag(
		@Query("cursor") cursor: Long?,
		@Query("direction") direction: Direction,
		@Query("sortOrder") sortOrder: SortOrder,
		@Query("limit") limit: Int,
		@Query("hashTag") hashTag: String
	): Connection<ShotDto>

	@GET("shots/private")
	fun getViewersPrivateConnection(
		@Query("cursor") cursor: Long?,
		@Query("direction") direction: Direction,
		@Query("sortOrder") sortOrder: SortOrder,
		@Query("limit") limit: Int
	): Flow<NetworkState<Connection<ShotDto>>>

	@GET("shots/bookmark")
	fun getViewerBookmarkConnection(
		@Query("cursor") cursor: Long?,
		@Query("direction") direction: Direction,
		@Query("sortOrder") sortOrder: SortOrder,
		@Query("limit") limit: Int
	): Flow<NetworkState<Connection<ShotDto>>>

	@GET("shots/following")
	fun getConnectionByFollowing(
		@Query("cursor") cursor: Long?,
		@Query("direction") direction: Direction,
		@Query("sortOrder") sortOrder: SortOrder,
		@Query("limit") limit: Int
	): Flow<NetworkState<Connection<ShotDto>>>

	@GET("shots")
	fun getConnectionByPersonId(
		@Query("personId") personId: Long,
		@Query("cursor") cursor: Long?,
		@Query("direction") direction: Direction,
		@Query("sortOrder") sortOrder: SortOrder,
		@Query("limit") limit: Int
	): Flow<NetworkState<Connection<ShotDto>>>

	@Multipart
	@POST("shots")
	fun postOne(
		@Part("args") args: ShotPostArgs,
		@Part files: List<MultipartBody.Part>
	): Flow<NetworkState<Unit>>

	@GET("shots/{shotId}")
	suspend fun getOneById(
		@Path("shotId") shotId: Long
	): ShotDto

	@DELETE("shots/{shotId}")
	suspend fun deleteOne(
		@Path("shotId") shotId: Long
	)

	@PUT("shots/{shotId}/bookmark")
	suspend fun putBookmark(@Path("shotId") shotId: Long)

	@PUT("shots/{shotId}/private")
	suspend fun putPrivate(@Path("shotId") shotId: Long)

	@PUT("shots/{shotId}/like")
	suspend fun putLikeOne(
		@Path("shotId") shotId: Long
	): LikeShotPayload

	@DELETE("shots/{shotId}/bookmark")
	suspend fun deleteBookmark(@Path("shotId") shotId: Long)

	@DELETE("shots/{shotId}/private")
	suspend fun deletePrivate(@Path("shotId") shotId: Long)

	@DELETE("shots/{shotId}/like")
	suspend fun deleteLikeOne(
		@Path("shotId") shotId: Long
	): LikeShotPayload
}