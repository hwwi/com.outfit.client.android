package com.outfit.client.android.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class CommentAndReplies(
	@Embedded
	val comment: Comment,
	@Relation(parentColumn = "id", entityColumn = "parent_id", entity = Comment::class)
	val replies: List<Comment>
)