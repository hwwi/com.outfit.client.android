package com.outfit.client.android.di.module

import com.outfit.client.android.data.DtoMapper
import com.outfit.client.android.data.model.Image
import com.outfit.client.android.db.Converters
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import java.util.*
import javax.inject.Singleton


@Module
class JsonModule {
	@Singleton
	@Provides
	fun provideMoshi(): Moshi {
		val moshi = Moshi.Builder()
			.add(KotlinJsonAdapterFactory())
			.add(Date::class.java, Rfc3339DateJsonAdapter())
			.build()

		Converters.jsonAdapter = moshi.adapter<List<Image>>(
			Types.newParameterizedType(
				MutableList::class.java,
				Image::class.java
			)
		)
		return moshi
	}

	@Singleton
	@Provides
	fun mappger(): DtoMapper = DtoMapper.INSTANCE

}