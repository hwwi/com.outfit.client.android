package com.outfit.client.android.db.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import com.outfit.client.android.data.CacheType
import com.outfit.client.android.data.DtoMapper
import com.outfit.client.android.data.dto.ShotDto
import com.outfit.client.android.data.model.Shot
import com.outfit.client.android.data.model.ShotBookmark
import com.outfit.client.android.data.model.ShotCacheId
import com.outfit.client.android.db.AbstractDao
import com.outfit.client.android.pref.SessionPref
import java.util.*

@Dao
abstract class ShotDao : AbstractDao<Shot, Long>() {
	@Query(
		"""
        SELECT *
          FROM shot
         WHERE id = :id
    """
	)
	abstract fun findOneById(id: Long): LiveData<Shot>

	@Query(
		"""
        SELECT *
          FROM shot
         WHERE EXISTS ( SELECT 1
		                  FROM shot_cache_id
		                 WHERE cache_type = :cacheType
		                   AND shot_id = shot.id )
         ORDER BY CAST(id AS INTEGER) DESC
    """
	)
	abstract fun findByCacheType(cacheType: CacheType): DataSource.Factory<Int, Shot>

	@Query(
		"""
        SELECT *
          FROM shot
         WHERE EXISTS ( SELECT 1
		                  FROM shot_cache_id
		                 WHERE cache_type = :cacheType
		                   AND shot_id = shot.id )
           AND person_id = :personId
		   AND is_private = :isPrivate
         ORDER BY CAST(id AS INTEGER) DESC
    """
	)
	abstract fun findByCacheTypeAndPersonId(
		cacheType: CacheType,
		personId: Long,
		isPrivate: Boolean
	): DataSource.Factory<Int, Shot>

	@Query(
		"""
        SELECT *
          FROM shot
		 INNER JOIN shot_bookmark ON shot.id = shot_bookmark.shot_id
         WHERE EXISTS ( SELECT 1
		                  FROM shot_cache_id
		                 WHERE cache_type = :cacheType
		                   AND shot_id = shot.id )
		   AND shot.is_viewer_bookmark = 1
         ORDER BY shot_bookmark.bookmarked_at DESC, shot.id DESC
    """
	)
	abstract fun findAllBookmarks(
		cacheType: CacheType = CacheType.BOOKMARKS
	): DataSource.Factory<Int, Shot>


	@Query(
		"""
        UPDATE shot
           SET likes_count    = :likesCount
		     , is_viewer_like = :isViewerLike
         WHERE id= :shotId
    """
	)
	abstract suspend fun updateOneLike(shotId: Long, likesCount: Int, isViewerLike: Boolean)

	@Query(
		"""
        UPDATE shot
           SET is_viewer_bookmark = :isViewerBookmark
         WHERE id= :shotId
    """
	)
	abstract suspend fun updateViewerBookmark(shotId: Long, isViewerBookmark: Boolean)

	@Query(
		"""
        UPDATE shot
           SET is_private = :isPrivate
         WHERE id= :shotId
    """
	)
	abstract suspend fun updatePrivate(shotId: Long, isPrivate: Boolean)

	@Query(
		"""
        UPDATE shot
           SET comments_count =  comments_count + :value
         WHERE id= :shotId
    """
	)
	abstract suspend fun incrementShotCommentsCount(shotId: Long, value: Int)

	@Query(
		"""
        UPDATE shot
           SET comments_count =  comments_count - :value
         WHERE id= :shotId
    """
	)
	abstract suspend fun decrementShotCommentsCount(shotId: Long, value: Int)

	@Query(
		"""
        DELETE
          FROM shot
         WHERE id= :shotId
    """
	)
	protected abstract suspend fun deleteOneByIdInternal(shotId: Long)

	@Query(
		"""
        DELETE
          FROM shot_cache_id
         WHERE shot_id= :shotId
    """
	)
	protected abstract suspend fun deleteCacheByIdInternal(shotId: Long)

	@Transaction
	open suspend fun deleteOneById(shotId: Long) {
		deleteOneByIdInternal(shotId)
		deleteCacheByIdInternal(shotId)
	}


	suspend fun insert(
		dto: ShotDto,
		cacheType: CacheType? = null
	) {
		insert(listOf(dto), cacheType)
	}

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	protected abstract suspend fun insertOrReplaceShotCacheIdsInternal(entities: List<ShotCacheId>): List<Long>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	protected abstract suspend fun insertOrReplaceShotBookmarksInternal(entities: List<ShotBookmark>): List<Long>

	@Insert(onConflict = OnConflictStrategy.IGNORE)
	protected abstract suspend fun insertOrIgnoreShotsInternal(entities: List<Shot>): List<Long>

	@Update
	protected abstract suspend fun updateShotsInternal(entities: List<Shot>)


	@Transaction
	open suspend fun insert(
		dtos: List<ShotDto>,
		cacheType: CacheType? = null
	) {
		if (dtos.isEmpty())
			return
		val shots = mutableListOf<Shot>()
		val shotCacheIds = mutableListOf<ShotCacheId>()
		val shotBookmarks = mutableListOf<ShotBookmark>()
		dtos.forEach { dto ->
			shots.add(DtoMapper.INSTANCE.map(dto))
			if (cacheType != null)
				shotCacheIds.add(ShotCacheId(dto.id, cacheType))
			if (cacheType == CacheType.BOOKMARKS && dto.bookmarkedAt != null)
				shotBookmarks.add(ShotBookmark(dto.id, dto.bookmarkedAt))
		}

		val insertResult = insertOrIgnoreShotsInternal(shots)
		val needUpdate = mutableListOf<Shot>()
		for (i in insertResult.indices) {
			if (insertResult[i] == -1L)
				needUpdate.add(shots[i])
		}
		if (needUpdate.isNotEmpty())
			updateShotsInternal(needUpdate)

		insertOrReplaceShotCacheIdsInternal(shotCacheIds)
		insertOrReplaceShotBookmarksInternal(shotBookmarks)
	}


	@Query(
		"""
		DELETE
		  FROM shot
		 WHERE NOT EXISTS ( SELECT 1
		                  FROM shot_cache_id
						 WHERE shot_id = shot.id)
	"""
	)
	protected abstract suspend fun deleteWhenNotExistsCache()

	@Query(
		"""
        DELETE
          FROM shot_cache_id
         WHERE cache_type = :cacheType
    """
	)
	protected abstract suspend fun deleteCacheInternal(cacheType: CacheType): Int

	@Transaction
	open suspend fun refresh(cacheType: CacheType, dtos: List<ShotDto>) {
		deleteCacheInternal(cacheType)
		deleteWhenNotExistsCache()
		insert(dtos, cacheType)
	}

	@Query(
		"""
        DELETE
          FROM shot_cache_id
         WHERE cache_type = :cacheType
		   AND EXISTS ( SELECT 1
		                  FROM shot
				         WHERE shot.id        = shot_cache_id.shot_id
				           AND shot.person_id = :personId
						   AND (:isPrivate IS NULL OR shot.is_private = :isPrivate)	)
    """
	)
	protected abstract suspend fun deleteCacheByPersonIdInternal(
		cacheType: CacheType,
		personId: Long,
		isPrivate: Boolean?
	): Int

	@Transaction
	open suspend fun refreshByPersonId(
		cacheType: CacheType,
		personId: Long,
		dtos: List<ShotDto>,
	) {
		deleteCacheByPersonIdInternal(cacheType, personId, null)
		deleteWhenNotExistsCache()
		insert(dtos, cacheType)
	}

	@Query("DELETE FROM shot_bookmark")
	protected abstract suspend fun deleteBookmarksInternal(): Int

	@Transaction
	open suspend fun refreshByViewerId(
		cacheType: CacheType,
		dtos: List<ShotDto>,
		isPrivate: Boolean? = null
	) {
		deleteCacheByPersonIdInternal(cacheType, SessionPref.id, isPrivate)
		deleteWhenNotExistsCache()
		if (cacheType == CacheType.BOOKMARKS)
			deleteBookmarksInternal()
		insert(dtos, cacheType)
	}
}
