package com.outfit.client.android.repository

import androidx.lifecycle.liveData
import androidx.lifecycle.map
import androidx.paging.DataSource
import com.outfit.client.android.data.NetworkState
import com.outfit.client.android.data.args.Direction
import com.outfit.client.android.data.args.PersonPostArgs
import com.outfit.client.android.data.args.SortOrder
import com.outfit.client.android.data.dto.PersonDto
import com.outfit.client.android.data.model.PersonDetail
import com.outfit.client.android.db.dao.PersonDetailDao
import com.outfit.client.android.network.api.PersonApi
import com.outfit.client.android.paging.CoroutineItemKeyedDataSource
import com.outfit.client.android.paging.InvalidateableDataSourceFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PersonRepository @Inject constructor(
	private val personDetailDao: PersonDetailDao,
	private val personApi: PersonApi
) : AbstractRepository() {

	fun findDetailOneById(id: Long) = liveData<NetworkState<PersonDetail>> {
		emitSource(personDetailDao.findOneById(id).map {
			NetworkState.fetching(it)
		})
		try {
			val dto = personApi.getDetailOneById(id)
			personDetailDao.insertOrReplace(dto)
			emitSource(personDetailDao.findOneById(id).map {
				NetworkState.success(it)
			})
		} catch (e: Exception) {
			emit(NetworkState.fail(e))
		}
	}

	fun findDetailOneByName(name: String) = liveData<NetworkState<PersonDetail>> {
		emitSource(personDetailDao.findOneByName(name).map {
			NetworkState.fetching(it)
		})
		try {
			val dto = personApi.getDetailOneByName(name)
			personDetailDao.insertOrReplace(dto)
			emitSource(personDetailDao.findOneByName(name).map {
				NetworkState.success(it)
			})
		} catch (e: Exception) {
			emit(NetworkState.fail(e))
		}
	}

	suspend fun putFollowing(personId: Long) {
		val payload = personApi.putFollowing(personId)
		personDetailDao.updateFollow(payload)
	}

	suspend fun deleteFollowing(personId: Long) {
		val payload = personApi.deleteFollowing(personId)
		personDetailDao.updateFollow(payload)
	}

	fun getFollowerConnection(
		coroutineScope: CoroutineScope,
		personId: Long,
		keyword: String?
	): InvalidateableDataSourceFactory<Long, PersonDto> =
		object : InvalidateableDataSourceFactory<Long, PersonDto>() {
			override fun onCreate(): DataSource<Long, PersonDto> =
				object : CoroutineItemKeyedDataSource<Long, PersonDto>(coroutineScope) {
					override fun getKey(item: PersonDto): Long = item.id
					override suspend fun onRequestSource(
						dataSourceDirection: DataSourceDirection,
						key: Long?,
						requestedLoadSize: Int
					): List<PersonDto> {
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
						return personApi
							.getFollowerConnection(
								personId,
								cursor,
								direction,
								sortOrder,
								requestedLoadSize,
								keyword
							)
							.edges
					}
				}
		}

	fun getFollowingConnection(
		coroutineScope: CoroutineScope,
		personId: Long,
		keyword: String?
	): InvalidateableDataSourceFactory<Long, PersonDto> =
		object : InvalidateableDataSourceFactory<Long, PersonDto>() {
			override fun onCreate(): DataSource<Long, PersonDto> =
				object : CoroutineItemKeyedDataSource<Long, PersonDto>(coroutineScope) {
					override fun getKey(item: PersonDto): Long = item.id
					override suspend fun onRequestSource(
						dataSourceDirection: DataSourceDirection,
						key: Long?,
						requestedLoadSize: Int
					): List<PersonDto> {
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
						return personApi
							.getFollowingConnection(
								personId,
								cursor,
								direction,
								sortOrder,
								requestedLoadSize,
								keyword
							)
							.edges

					}
				}
		}

	fun getValidateName(name: String): Flow<NetworkState<Unit>> =
		personApi.getValidateName(name)

	fun postPerson(args: PersonPostArgs): Flow<NetworkState<Unit>> =
		personApi.postPerson(args)
}