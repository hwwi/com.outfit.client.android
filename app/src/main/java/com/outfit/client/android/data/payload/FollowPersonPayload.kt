package com.outfit.client.android.data.payload

data class FollowPersonPayload(
	val followerId: Long,
	val followerFollowingsCount: Int,
	val followedId: Long,
	val followedFollowersCount: Int,
	val isFollow: Boolean
)