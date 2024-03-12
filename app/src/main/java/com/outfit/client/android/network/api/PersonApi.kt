package com.outfit.client.android.network.api

import com.outfit.client.android.data.NetworkState
import com.outfit.client.android.data.args.Direction
import com.outfit.client.android.data.args.PersonPostArgs
import com.outfit.client.android.data.args.SortOrder
import com.outfit.client.android.data.dto.Connection
import com.outfit.client.android.data.dto.PersonDetailDto
import com.outfit.client.android.data.dto.PersonDto
import com.outfit.client.android.data.payload.FollowPersonPayload
import kotlinx.coroutines.flow.Flow
import retrofit2.http.*

interface PersonApi {
	@GET("persons/validate")
	fun getValidateName(@Query("name") name: String): Flow<NetworkState<Unit>>

	@POST("persons")
	fun postPerson(@Body args: PersonPostArgs): Flow<NetworkState<Unit>>

	@GET("persons/{id}")
	suspend fun getOneById(@Path("id") id: Long): PersonDto

	@GET("persons/detail/{id}")
	suspend fun getDetailOneById(@Path("id") id: Long): PersonDetailDto

	@GET("persons/detail")
	suspend fun getDetailOneByName(@Query("name") personName: String): PersonDetailDto

	@GET("persons/follower/{personId}")
	suspend fun getFollowerConnection(
		@Path("personId") personId: Long,
		@Query("cursor") cursor: Long?,
		@Query("direction") direction: Direction,
		@Query("sortOrder") sortOrder: SortOrder,
		@Query("limit") limit: Int,
		@Query("keyword") keyword: String?
	): Connection<PersonDto>

	@GET("persons/following/{personId}")
	suspend fun getFollowingConnection(
		@Path("personId") personId: Long,
		@Query("cursor") cursor: Long?,
		@Query("direction") direction: Direction,
		@Query("sortOrder") sortOrder: SortOrder,
		@Query("limit") limit: Int,
		@Query("keyword") keyword: String?
	): Connection<PersonDto>

	@PUT("persons/following/{personId}")
	suspend fun putFollowing(
		@Path("personId") personId: Long
	): FollowPersonPayload

	@DELETE("persons/following/{personId}")
	suspend fun deleteFollowing(
		@Path("personId") personId: Long
	): FollowPersonPayload
}