package com.outfit.client.android.data

enum class AspectRatio(
	val x: Int,
	val y: Int,
	val displayText: String = "${x}:${y}"
) {
	Proportion5Over4(5, 4),
	Proportion4Over5(4, 5),
	Proportion1Over1(1, 1),
	Proportion1Dot91Over1(191, 100, "1.91:1"),
}