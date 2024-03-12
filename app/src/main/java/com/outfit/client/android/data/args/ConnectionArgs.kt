package com.outfit.client.android.data.args

import androidx.paging.PagingRequestHelper
import com.outfit.client.android.data.IEntity
import com.outfit.client.android.data.model.Shot

data class ConnectionArgs(
	val cursor: Long?,
	val direction: Direction,
	val sortOrder: SortOrder,
	val limit: Int
) {

	companion object {
		fun forward(
			requestType: PagingRequestHelper.RequestType,
			item: IEntity<Long>?,
			networkPageSize: Int
		): ConnectionArgs {
			val direction: Direction
			val sortOrder: SortOrder
			when (requestType) {
				PagingRequestHelper.RequestType.INITIAL,
				PagingRequestHelper.RequestType.AFTER -> {
					direction = Direction.AFTER
					sortOrder = SortOrder.ASCENDING
				}
				PagingRequestHelper.RequestType.BEFORE -> {
					direction = Direction.PREVIOUS
					sortOrder = SortOrder.DESCENDING
				}
			}
			return ConnectionArgs(item?.id, direction, sortOrder, networkPageSize)
		}

		fun reverse(
			requestType: PagingRequestHelper.RequestType,
			item: IEntity<Long>?,
			networkPageSize: Int
		): ConnectionArgs {
			val direction: Direction
			val sortOrder: SortOrder
			when (requestType) {
				PagingRequestHelper.RequestType.INITIAL,
				PagingRequestHelper.RequestType.AFTER -> {
					direction = Direction.PREVIOUS
					sortOrder = SortOrder.DESCENDING
				}
				PagingRequestHelper.RequestType.BEFORE -> {
					direction = Direction.AFTER
					sortOrder = SortOrder.ASCENDING
				}
			}
			return ConnectionArgs(item?.id, direction, sortOrder, networkPageSize)
		}
	}
}