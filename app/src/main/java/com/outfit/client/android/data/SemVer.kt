package com.outfit.client.android.data

data class SemVer(
	val major: Int,
	val minor: Int,
	val patch: Int
) {
	constructor(versionCode: Int) : this(
		versionCode / 1_000_000,
		versionCode % 1_000_000 / 1_000,
		versionCode % 1_000,
	)
}