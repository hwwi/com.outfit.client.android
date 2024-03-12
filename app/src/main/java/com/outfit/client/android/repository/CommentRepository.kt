package com.outfit.client.android.repository

import androidx.paging.DataSource
import androidx.room.withTransaction
import com.outfit.client.android.data.DtoMapper
import com.outfit.client.android.data.NetworkState
import com.outfit.client.android.data.args.CommentPostArgs
import com.outfit.client.android.data.args.ConnectionArgs
import com.outfit.client.android.data.model.Comment
import com.outfit.client.android.db.NetworkCacheDatabase
import com.outfit.client.android.db.dao.CommentDao
import com.outfit.client.android.db.dao.ShotDao
import com.outfit.client.android.extension.ignoreData
import com.outfit.client.android.extension.onSuccess
import com.outfit.client.android.network.api.CommentApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

class CommentRepository @Inject constructor(
	private val commentApi: CommentApi,
	private val commentDao: CommentDao,
	private val shotDao: ShotDao,
	private val dtoMapper: DtoMapper,
	private val database: NetworkCacheDatabase
) {
	fun refreshCommentConnectionByShotId(
		shotId: Long,
		connectionArgs: ConnectionArgs
	): Flow<NetworkState<Unit>> =
		commentApi
			.getConnection(
				shotId,
				connectionArgs.cursor,
				connectionArgs.direction,
				connectionArgs.sortOrder,
				connectionArgs.limit
			)
			.onSuccess {
				commentDao.refreshByShotId(shotId, it.edges)
			}
			.ignoreData()

	fun fetchCommentConnectionByShotId(
		shotId: Long,
		connectionArgs: ConnectionArgs
	): Flow<NetworkState<Unit>> =
		commentApi
			.getConnection(
				shotId,
				connectionArgs.cursor,
				connectionArgs.direction,
				connectionArgs.sortOrder,
				connectionArgs.limit
			)
			.onSuccess {
				commentDao.insertOrReplaceDtoCompletable(it.edges)
			}
			.ignoreData()


	fun findCachedDataSourceFactoryByShotId(shotId: Long): DataSource.Factory<Int, Comment> =
		commentDao.commentsByShotId(shotId)

	suspend fun delete(shotId: Long, commentId: Long) {
		commentApi.deleteOne(shotId, commentId)
		database.withTransaction {
			commentDao.deleteOneById(commentId)
			shotDao.decrementShotCommentsCount(shotId, 1)
		}
	}

	suspend fun putLike(shotId: Long, commentId: Long) {
		val payload = commentApi.putLikeOne(shotId, commentId)
		commentDao.updateCommentLike(payload.commentId, payload.likesCount, payload.isViewerLike)
	}

	suspend fun deleteLike(shotId: Long, commentId: Long) {
		val payload = commentApi.deleteLikeOne(shotId, commentId)
		commentDao.updateCommentLike(payload.commentId, payload.likesCount, payload.isViewerLike)
	}

	suspend fun postNewComment(
		shotId: Long,
		targetReplyComment: Comment?,
		text: String
	) {
		val dto = when (val commentId = targetReplyComment?.id) {
			null -> commentApi.postOne(shotId, CommentPostArgs(text))
			else -> commentApi.postReplyOne(shotId, commentId, CommentPostArgs(text))
		}
		commentDao.insertOrReplaceDtoCompletable(dto)
		shotDao.incrementShotCommentsCount(shotId, 1)
	}
}