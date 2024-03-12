package com.outfit.client.android.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.outfit.client.android.data.DtoMapper
import com.outfit.client.android.data.dto.PersonDetailDto
import com.outfit.client.android.data.model.PersonDetail
import com.outfit.client.android.data.payload.FollowPersonPayload
import com.outfit.client.android.db.AbstractDao

@Dao
abstract class PersonDetailDao : AbstractDao<PersonDetail, Long>() {

	@Query(
		"""
		SELECT *
		  FROM person_detail
		 WHERE name = :name
	"""
	)
	abstract fun findOneByName(name: String): LiveData<PersonDetail>

	@Query(
		"""
		SELECT *
		  FROM person_detail
		 WHERE id = :id
	"""
	)
	abstract fun findOneById(id: Long): LiveData<PersonDetail>

	@Query(
		"""
        UPDATE person_detail
           SET name = :name
         WHERE id= :personId
    """
	)
	abstract suspend fun updateName(personId: Long, name: String)

	@Query(
		"""
        UPDATE person_detail
           SET biography = :biography
         WHERE id= :personId
    """
	)
	abstract suspend fun updateBiography(personId: Long, biography: String)

	@Query(
		"""
        UPDATE person_detail
           SET profile_image_url = :profileImageUrl
         WHERE id= :personId
    """
	)
	abstract suspend fun setProfileImage(personId: Long, profileImageUrl: String?)


	@Query(
		"""
        UPDATE person_detail
           SET closet_background_image_url = :closetBackgroundImageUrl
         WHERE id= :personId
    """
	)
	abstract suspend fun setClosetBackgroundImage(personId: Long, closetBackgroundImageUrl: String?)


	@Query(
		"""
        UPDATE person_detail
           SET followings_count = :followerFollowingsCount
         WHERE id = :followerId
    """
	)
	protected abstract suspend fun updateFollower(
		followerId: Long,
		followerFollowingsCount: Int
	)

	@Query(
		"""
        UPDATE person_detail
           SET followers_count = :followedFollowersCount
		     , is_viewer_follow = :isFollow
         WHERE id = :followedId
    """
	)
	protected abstract suspend fun updateFollowed(
		followedId: Long,
		followedFollowersCount: Int,
		isFollow: Boolean
	)

	@Transaction
	open suspend fun updateFollow(payload: FollowPersonPayload) {
		updateFollower(payload.followerId, payload.followerFollowingsCount)
		updateFollowed(payload.followedId, payload.followedFollowersCount, payload.isFollow)
	}


	@Insert(onConflict = OnConflictStrategy.REPLACE)
	protected abstract suspend fun insertOrReplace(entity: PersonDetail)

	suspend fun insertOrReplace(dto: PersonDetailDto) = insertOrReplace(DtoMapper.INSTANCE.map(dto))
}
