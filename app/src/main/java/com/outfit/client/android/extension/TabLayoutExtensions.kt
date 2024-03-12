package com.outfit.client.android.extension

import com.google.android.material.tabs.TabLayout

val TabLayout.selectedTab: TabLayout.Tab?
	get() = getTabAt(selectedTabPosition)