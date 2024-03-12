package com.outfit.client.android.data

enum class SelectImagePurpose(
	val defaultRatio: AspectRatio,
	val selectableRatios: List<AspectRatio> = listOf(defaultRatio),
	val maxImageCount: Int = 1
) {
	Profile(AspectRatio.Proportion1Over1),
	ClosetBackground(AspectRatio.Proportion5Over4),
	Shot(
		AspectRatio.Proportion4Over5,
		listOf(
			AspectRatio.Proportion4Over5,
			AspectRatio.Proportion1Over1,
			AspectRatio.Proportion1Dot91Over1
		),
		5
	),
}