package com.outfit.client.android.db.dao

import androidx.paging.DataSource
import androidx.room.*
import com.outfit.client.android.data.DtoMapper
import com.outfit.client.android.data.dto.NotificationDto
import com.outfit.client.android.data.model.Notification
import com.outfit.client.android.db.AbstractDao

@Dao
abstract class NotificationDao : AbstractDao<Notification, Long>() {
	@Query(
		"""
        SELECT *
          FROM notification
         ORDER BY CAST(id AS INTEGER) desc
    """
	)
	abstract fun findAll(): DataSource.Factory<Int, Notification>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	protected abstract suspend fun insertOrReplaceInternal(entities: List<Notification>): List<Long>

	suspend fun insertOrReplace(dtos: List<NotificationDto>) =
		insertOrReplaceInternal(dtos.map { DtoMapper.INSTANCE.map(it) })

	@Query(
		"""
		DELETE
		  FROM notification
	"""
	)
	protected abstract suspend fun deleteAllInternal()

	@Transaction
	open suspend fun refresh(dtos: List<NotificationDto>) {
		deleteAllInternal()
		insertOrReplace(dtos)
	}

	@Query(
		"""
		DELETE
		  FROM notification
		 WHERE id = :notificationId
	"""
	)
	abstract suspend fun delete(notificationId: Long)
}
