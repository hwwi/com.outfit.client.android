package com.outfit.client.android.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import androidx.paging.DataSource
import com.outfit.client.android.data.CacheType
import com.outfit.client.android.data.DtoMapper
import com.outfit.client.android.data.NetworkState
import com.outfit.client.android.data.args.ConnectionArgs
import com.outfit.client.android.data.args.Direction
import com.outfit.client.android.data.args.SortOrder
import com.outfit.client.android.data.model.Shot
import com.outfit.client.android.db.dao.ShotDao
import com.outfit.client.android.extension.ignoreData
import com.outfit.client.android.extension.onSuccess
import com.outfit.client.android.network.api.ShotApi
import com.outfit.client.android.paging.CoroutineItemKeyedDataSource
import com.outfit.client.android.paging.InvalidateableDataSourceFactory
import com.outfit.client.android.pref.SessionPref
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ShotRepository @Inject constructor(
	private val shotApi: ShotApi,
	private val shotDao: ShotDao,
	private val dtoMapper: DtoMapper
) : AbstractRepository() {


	fun findCachedOneById(
		shotId: Long,
		cacheType: CacheType? = null
	): LiveData<NetworkState<Shot>> = liveData {
		emitSource(shotDao.findOneById(shotId).map {
			NetworkState.fetching(it)
		})
		try {
			val dto = shotApi.getOneById(shotId)
			shotDao.insert(dto, cacheType)
			emitSource(shotDao.findOneById(shotId).map {
				NetworkState.success(it)
			})
		} catch (e: Exception) {
			emit(NetworkState.fail<Shot>(e))
		}
	}

	fun find(cacheType: CacheType): DataSource.Factory<Int, Shot> =
		shotDao.findByCacheType(cacheType)

	fun findAllBookmarks(): DataSource.Factory<Int, Shot> = shotDao.findAllBookmarks()

	fun getConnectionByBrandProductTag(
		coroutineScope: CoroutineScope,
		brand: String,
		product: String?
	): InvalidateableDataSourceFactory<Long, Shot> =
		object : InvalidateableDataSourceFactory<Long, Shot>() {
			override fun onCreate(): DataSource<Long, Shot> =
				object : CoroutineItemKeyedDataSource<Long, Shot>(coroutineScope) {
					override fun getKey(item: Shot): Long = item.id
					override suspend fun onRequestSource(
						dataSourceDirection: DataSourceDirection,
						key: Long?,
						requestedLoadSize: Int
					): List<Shot> {
						val cursor: Long?
						val direction: Direction
						val sortOrder: SortOrder
						when (dataSourceDirection) {
							DataSourceDirection.Initial -> {
								// always load from the beginning.
								cursor = null
								direction = Direction.PREVIOUS
								sortOrder = SortOrder.DESCENDING
							}
							DataSourceDirection.After -> {
								cursor = key
								direction = Direction.PREVIOUS
								sortOrder = SortOrder.DESCENDING
							}
							DataSourceDirection.Before -> {
								cursor = key
								direction = Direction.AFTER
								sortOrder = SortOrder.ASCENDING
							}
						}
						return shotApi
							.getConnectionByBrandProductTag(
								cursor,
								direction,
								sortOrder,
								requestedLoadSize,
								brand,
								product
							)
							.edges.map { dtoMapper.map(it) }
					}
				}
		}

	fun getConnectionByHashTag(
		coroutineScope: CoroutineScope,
		hashTag: String
	): InvalidateableDataSourceFactory<Long, Shot> =
		object : InvalidateableDataSourceFactory<Long, Shot>() {
			override fun onCreate(): DataSource<Long, Shot> =
				object : CoroutineItemKeyedDataSource<Long, Shot>(coroutineScope) {
					override fun getKey(item: Shot): Long = item.id
					override suspend fun onRequestSource(
						dataSourceDirection: DataSourceDirection,
						key: Long?,
						requestedLoadSize: Int
					): List<Shot> {
						val cursor: Long?
						val direction: Direction
						val sortOrder: SortOrder
						when (dataSourceDirection) {
							DataSourceDirection.Initial -> {
								// always load from the beginning.
								cursor = null
								direction = Direction.PREVIOUS
								sortOrder = SortOrder.DESCENDING
							}
							DataSourceDirection.After -> {
								cursor = key
								direction = Direction.PREVIOUS
								sortOrder = SortOrder.DESCENDING
							}
							DataSourceDirection.Before -> {
								cursor = key
								direction = Direction.AFTER
								sortOrder = SortOrder.ASCENDING
							}
						}
						return shotApi
							.getConnectionByHashTag(
								cursor,
								direction,
								sortOrder,
								requestedLoadSize,
								hashTag
							)
							.edges.map { dtoMapper.map(it) }
					}
				}
		}

	fun findByPersonId(
		cacheType: CacheType,
		personId: Long,
		isPrivate: Boolean
	): DataSource.Factory<Int, Shot> =
		shotDao.findByCacheTypeAndPersonId(cacheType, personId, isPrivate)

	fun fetchConnection(
		cacheType: CacheType,
		connectionArgs: ConnectionArgs,
		isRefresh: Boolean = false
	): Flow<NetworkState<Unit>> =
		shotApi
			.getConnection(
				connectionArgs.cursor,
				connectionArgs.direction,
				connectionArgs.sortOrder,
				connectionArgs.limit
			)
			.onSuccess { connection ->
				when (isRefresh) {
					true -> shotDao.refresh(cacheType, connection.edges)
					false -> shotDao.insert(connection.edges, cacheType)
				}
			}
			.ignoreData()

	fun fetchConnectionByPersonId(
		cacheType: CacheType,
		personId: Long,
		connectionArgs: ConnectionArgs,
		isRefresh: Boolean
	): Flow<NetworkState<Unit>> =
		shotApi
			.getConnectionByPersonId(
				personId,
				connectionArgs.cursor,
				connectionArgs.direction,
				connectionArgs.sortOrder,
				connectionArgs.limit
			)
			.onSuccess { connection ->
				when (isRefresh) {
					true -> shotDao.refreshByPersonId(cacheType, personId, connection.edges)
					false -> shotDao.insert(connection.edges, cacheType)
				}
			}
			.ignoreData()

	fun fetchViewersPrivateConnection(
		cacheType: CacheType,
		connectionArgs: ConnectionArgs,
		isRefresh: Boolean
	): Flow<NetworkState<Unit>> {
		return shotApi
			.getViewersPrivateConnection(
				connectionArgs.cursor,
				connectionArgs.direction,
				connectionArgs.sortOrder,
				connectionArgs.limit
			)
			.onSuccess { connection ->
				when (isRefresh) {
					true -> shotDao.refreshByViewerId(
						cacheType,
						connection.edges,
						true
					)
					false -> shotDao.insert(connection.edges, cacheType)
				}
			}
			.ignoreData()
	}

	fun fetchViewerBookmarkConnection(
		connectionArgs: ConnectionArgs,
		isRefresh: Boolean
	): Flow<NetworkState<Unit>> {
		return shotApi
			.getViewerBookmarkConnection(
				connectionArgs.cursor,
				connectionArgs.direction,
				connectionArgs.sortOrder,
				connectionArgs.limit
			)
			.onSuccess { connection ->
				when (isRefresh) {
					true -> shotDao.refreshByViewerId(
						CacheType.BOOKMARKS,
						connection.edges
					)
					false -> shotDao.insert(connection.edges, CacheType.BOOKMARKS)
				}
			}
			.ignoreData()
	}

	fun fetchConnectionByFollowing(
		cacheType: CacheType,
		connectionArgs: ConnectionArgs,
		isRefresh: Boolean = false
	): Flow<NetworkState<Unit>> =
		shotApi
			.getConnectionByFollowing(
				connectionArgs.cursor,
				connectionArgs.direction,
				connectionArgs.sortOrder,
				connectionArgs.limit
			)
			.onSuccess { connection ->
				when (isRefresh) {
					true -> shotDao.refresh(cacheType, connection.edges)
					false -> shotDao.insert(connection.edges, cacheType)
				}
			}
			.ignoreData()

	suspend fun delete(shotId: Long) {
		shotApi.deleteOne(shotId)
		shotDao.deleteOneById(shotId)
	}

	suspend fun putBookmark(shotId: Long) {
		shotApi.putBookmark(shotId)
		shotDao.updateViewerBookmark(shotId, true)
	}


	suspend fun putPrivate(shotId: Long) {
		shotApi.putPrivate(shotId)
		shotDao.updatePrivate(shotId, true)
	}

	suspend fun putLike(shotId: Long) {
		val payload = shotApi.putLikeOne(shotId)
		shotDao.updateOneLike(payload.shotId, payload.likesCount, payload.isViewerLike)
	}

	suspend fun deleteBookmark(shotId: Long) {
		shotApi.deleteBookmark(shotId)
		shotDao.updateViewerBookmark(shotId, false)
	}

	suspend fun deletePrivate(shotId: Long) {
		shotApi.deletePrivate(shotId)
		shotDao.updatePrivate(shotId, false)
	}

	suspend fun deleteLike(shotId: Long) {
		val payload = shotApi.deleteLikeOne(shotId)
		shotDao.updateOneLike(payload.shotId, payload.likesCount, payload.isViewerLike)
	}
}

