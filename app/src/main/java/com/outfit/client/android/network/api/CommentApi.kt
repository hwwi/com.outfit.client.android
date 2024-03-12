package com.outfit.client.android.network.api

import com.outfit.client.android.data.NetworkState
import com.outfit.client.android.data.args.CommentPostArgs
import com.outfit.client.android.data.args.Direction
import com.outfit.client.android.data.args.SortOrder
import com.outfit.client.android.data.dto.CommentDto
import com.outfit.client.android.data.dto.Connection
import com.outfit.client.android.data.payload.LikeCommentPayload
import kotlinx.coroutines.flow.Flow
import retrofit2.http.*

interface CommentApi {
	@GET("shots/{shotId}/comments")
	fun getConnection(
		@Path("shotId") shotId: Long,
		@Query("cursor") cursor: Long?,
		@Query("direction") direction: Direction,
		@Query("sortOrder") sortOrder: SortOrder,
		@Query("limit") limit: Int
	): Flow<NetworkState<Connection<CommentDto>>>

	@POST("shots/{shotId}/comments")
	suspend fun postOne(
		@Path("shotId") shotId: Long,
		@Body body: CommentPostArgs
	): CommentDto

	@POST("shots/{shotId}/comments/reply/{commentId}")
	suspend fun postReplyOne(
		@Path("shotId") shotId: Long,
		@Path("commentId") commentId: Long,
		@Body body: CommentPostArgs
	): CommentDto

	@GET("shots/{shotId}/comments/{commentId}")
	suspend fun getOne(
		@Path("shotId") shotId: Long,
		@Path("commentId") commentId: Long
	): CommentDto

	@DELETE("shots/{shotId}/comments/{commentId}")
	suspend fun deleteOne(
		@Path("shotId") shotId: Long,
		@Path("commentId") commentId: Long
	)

	@PUT("shots/{shotId}/comments/{commentId}/like")
	suspend fun putLikeOne(
		@Path("shotId") shotId: Long,
		@Path("commentId") commentId: Long
	): LikeCommentPayload

	@DELETE("shots/{shotId}/comments/{commentId}/like")
	suspend fun deleteLikeOne(
		@Path("shotId") shotId: Long,
		@Path("commentId") commentId: Long
	): LikeCommentPayload
}