package com.outfit.client.android.di.module

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.outfit.client.android.BuildConfig
import com.outfit.client.android.data.args.AuthPostRefreshTokenArgs
import com.outfit.client.android.network.FlowNetworkStateCallAdapter
import com.outfit.client.android.network.api.*
import com.outfit.client.android.pref.IdPref
import com.outfit.client.android.pref.SessionPref
import com.squareup.moshi.Moshi
import dagger.Lazy
import dagger.Module
import dagger.Provides
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import java.util.*
import javax.inject.Singleton


@Module(includes = [NetworkModule.ProvidesModule::class])
interface NetworkModule {
	companion object {
		const val HEADER_AUTHORIZATION = "Authorization"
		const val HEADER_APP_UUID = "AppUuid"
		const val HEADER_ACCEPT_LANGUAGE = "Accept-Language"
		const val ACTION_SESSION_EXPIRED =
			"com.outfit.client.android.di.module.NetworkModule.ACTION_SESSION_EXPIRED"
	}

	@Module
	class ProvidesModule {

		@Singleton
		@Provides
		fun provideOkHttpClient(
			context: Context,
			authApi: Lazy<AuthApi>
		): OkHttpClient = OkHttpClient.Builder().apply {
			cache(Cache(context.cacheDir, 10 * 1024 * 1024))
			authenticator(object : Authenticator {
				override fun authenticate(route: Route?, response: Response): Request? {
					val accessToken = SessionPref.accessToken
					val refreshToken = SessionPref.refreshToken
					if (accessToken == null || refreshToken == null) {
						sendSessionExpiredBroadCast()
						return null
					}

					synchronized(this) {
						val newAccessToken = SessionPref.accessToken
						if (newAccessToken == null) {
							sendSessionExpiredBroadCast()
							return null
						}

						// AccessToken is refreshed in another thread.
						if (accessToken != newAccessToken)
							return response.request.newBuilder()
								.header(HEADER_AUTHORIZATION, newAccessToken)
								.build()

						// Need to refresh an accessToken
						val refreshTokenResponse = authApi.get()
							.postRefreshToken(AuthPostRefreshTokenArgs(accessToken, refreshToken))
							.execute()
						val body = refreshTokenResponse.body()
						if (body == null) {
							sendSessionExpiredBroadCast()
							return null
						}

						SessionPref.accessToken = body.accessToken
						SessionPref.refreshToken = body.refreshToken

						return response.request.newBuilder()
							.header(HEADER_AUTHORIZATION, body.accessToken)
							.build()
					}
				}

				fun sendSessionExpiredBroadCast() {
					LocalBroadcastManager.getInstance(context)
						.sendBroadcast(Intent(ACTION_SESSION_EXPIRED))
				}
			})
			addInterceptor { chain ->
				val request = chain.request().newBuilder()
					.apply {
						header(
							"Accept",
							"application/problem+json; application/validation.problem+json"
						)
						//TODO 직접 필요한곳에서 파라미터로 appUuid 사용 할 것.
						header(HEADER_APP_UUID, IdPref.appUuid)
						header(
							HEADER_ACCEPT_LANGUAGE, when {
								Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ->
									Locale.getDefault().toLanguageTag()
								else ->
									Locale.getDefault().language
							}
						)
						val token = SessionPref.accessToken
						if (token != null)
							header(HEADER_AUTHORIZATION, token)
					}
					.build()
				chain.proceed(request)
			}
			// https://github.com/square/retrofit/issues/2867
			// https://github.com/square/retrofit/issues/3075
			addNetworkInterceptor {
				val response = it.proceed(it.request())
				if (response.isSuccessful.not() || response.code !in 204..205)
					return@addNetworkInterceptor response
				response
					.newBuilder()
					.code(200)
					.body("".toResponseBody("text/plain".toMediaType()))
					.build()
			}
			if (BuildConfig.DEBUG) {
				addInterceptor(
					HttpLoggingInterceptor()
						.apply {
							level = HttpLoggingInterceptor.Level.BODY
						})
				addInterceptor(ChuckerInterceptor(context))
				addNetworkInterceptor(StethoInterceptor())
			}
		}.build()


		@Singleton
		@Provides
		fun provideRetrofit(
			okHttpClient: OkHttpClient,
			moshi: Moshi,
			flowNetworkStateCallAdapterFactory: FlowNetworkStateCallAdapter.Factory
		): Retrofit =
			Retrofit.Builder().apply {
				baseUrl(BuildConfig.URL_API_OUTFIT)
				client(okHttpClient)
				addConverterFactory(MoshiConverterFactory.create(moshi))
				addCallAdapterFactory(flowNetworkStateCallAdapterFactory)
			}.build()

		@Singleton
		@Provides
		fun provideAuthApi(retrofit: Retrofit): AuthApi = retrofit.create()

		@Singleton
		@Provides
		fun provideAccountApi(retrofit: Retrofit): AccountApi = retrofit.create()

		@Singleton
		@Provides
		fun providePersonApi(retrofit: Retrofit): PersonApi = retrofit.create()

		@Singleton
		@Provides
		fun provideShotApi(retrofit: Retrofit): ShotApi = retrofit.create()

		@Singleton
		@Provides
		fun provideCommentApi(retrofit: Retrofit): CommentApi = retrofit.create()

		@Singleton
		@Provides
		fun provideSearchApi(retrofit: Retrofit): SearchApi = retrofit.create()

		@Singleton
		@Provides
		fun provideVerificationApi(retrofit: Retrofit): VerificationApi = retrofit.create()

		@Singleton
		@Provides
		fun provideTagApi(retrofit: Retrofit): TagApi = retrofit.create()

		@Singleton
		@Provides
		fun provideCloudMessagingApi(retrofit: Retrofit): NotificationApi = retrofit.create()

	}
}