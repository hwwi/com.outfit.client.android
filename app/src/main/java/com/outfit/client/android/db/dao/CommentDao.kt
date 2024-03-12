package com.outfit.client.android.db.dao

import androidx.paging.DataSource
import androidx.room.*
import com.outfit.client.android.data.DtoMapper
import com.outfit.client.android.data.dto.CommentDto
import com.outfit.client.android.data.model.Comment
import com.outfit.client.android.db.AbstractDao
import kotlinx.coroutines.flow.Flow


@Dao
abstract class CommentDao : AbstractDao<Comment, Long>() {

	@Query(
		"""
    			SELECT *
				  FROM comment
				 WHERE shot_id = :shotId
				 ORDER BY COALESCE(parent_id, id), id
    """
	)
	abstract fun commentsByShotId(shotId: Long): DataSource.Factory<Int, Comment>

	@Query(
		"""
        SELECT *
          FROM comment
         WHERE id = :id
    """
	)
	abstract fun findOneById(id: Long): Flow<Comment>

	@Query(
		"""
        DELETE
          FROM comment
         WHERE id= :commentId
    """
	)
	abstract suspend fun deleteOneById(commentId: Long): Int

	@Query(
		"""
        UPDATE comment
           SET likes_count    = :likesCount
             , is_viewer_like = :isViewerLike
         WHERE id= :commentId
    """
	)
	abstract suspend fun updateCommentLike(commentId: Long, likesCount: Int, isViewerLike: Boolean)

	@Query(
		"""
        DELETE
          FROM comment
         WHERE shot_id= :shotId
    """
	)
	protected abstract suspend fun deleteByShotIdInternal(shotId: Long): Int


	@Insert(onConflict = OnConflictStrategy.REPLACE)
	protected abstract suspend fun insertOrReplaceInternal(entities: List<Comment>): List<Long>


	protected suspend fun insertOrReplaceDto(dtos: List<CommentDto>) =
		insertOrReplaceInternal(dtos.map { DtoMapper.INSTANCE.map(it) })

	suspend fun insertOrReplaceDtoCompletable(dto: CommentDto) =
		insertOrReplaceDtoCompletable(listOf(dto))

	suspend fun insertOrReplaceDtoCompletable(dtos: List<CommentDto>) =
		insertOrReplaceDto(dtos)

	@Transaction
	open suspend fun refreshByShotId(shotId: Long, dtos: List<CommentDto>) {
		deleteByShotIdInternal(shotId)
		insertOrReplaceDto(dtos)
	}
}
