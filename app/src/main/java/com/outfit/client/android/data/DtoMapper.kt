package com.outfit.client.android.data

import com.outfit.client.android.data.dto.*
import com.outfit.client.android.data.model.*
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.mapstruct.ReportingPolicy
import org.mapstruct.factory.Mappers

@Mapper(
	unmappedTargetPolicy = ReportingPolicy.ERROR
)
abstract class DtoMapper {
	companion object {
		val INSTANCE: DtoMapper = Mappers.getMapper(
			DtoMapper::class.java
		)
	}


	protected abstract fun mapInternal(dto: PersonDto): Person
	protected abstract fun mapInternal(dto: ImageDto): Image

	protected abstract fun mapInternal(dto: ItemTagDto): ItemTag

	@Mappings(
		Mapping(source = "viewerFollow", target = "isViewerFollow")
	)
	abstract fun map(dto: PersonDetailDto): PersonDetail


	@Mappings(
		Mapping(source = "private", target = "isPrivate"),
		Mapping(source = "viewerBookmark", target = "isViewerBookmark"),
		Mapping(source = "viewerLike", target = "isViewerLike")
	)
	abstract fun map(dto: ShotDto): Shot

	@Mappings(
		Mapping(source = "viewerLike", target = "isViewerLike")
	)
	abstract fun map(dto: CommentDto): Comment

	abstract fun map(dto: NotificationDto): Notification

}