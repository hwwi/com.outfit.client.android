package com.outfit.client.android

import com.airbnb.epoxy.PackageModelViewConfig

@PackageModelViewConfig(
	rClass = R::class,
	useLayoutOverloads = true,
	defaultLayoutPattern = "layout_%s"
)
interface EpoxyConfig