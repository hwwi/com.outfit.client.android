package com.outfit.client.android.network

import com.outfit.client.android.data.NetworkState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.awaitResponse
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import javax.inject.Inject
import javax.inject.Singleton

class FlowNetworkStateCallAdapter<R> private constructor(
	private val responseType: Type,
	private val responseErrorParser: ResponseErrorParser
) : CallAdapter<R, Any> {

	override fun responseType(): Type {
		return responseType
	}

	override fun adapt(call: Call<R>): Any {
		return flow {
			emit(NetworkState.fetching())

			val response = try {
				call.awaitResponse()
			} catch (e: Throwable) {
				emit(NetworkState.fail(e))
				return@flow
			}

			val body = response.body()
			emit(
				when {
					!response.isSuccessful -> NetworkState.fail(
						responseErrorParser.parseError(response)
					)
					responseType == Unit::class.java -> NetworkState.success(Unit)
					body == null -> NetworkState.fail(IllegalArgumentException("expected $responseType type body, but body is null"))
					else -> NetworkState.success(body)
				}
			)
		}
	}

	@Singleton
	class Factory @Inject constructor(
		private val responseErrorParser: ResponseErrorParser
	) : CallAdapter.Factory() {

		override fun get(
			returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit
		): CallAdapter<*, *>? {
			val rawType = getRawType(returnType)
			if (rawType != Flow::class.java)
				return null

			check(returnType is ParameterizedType) { "Flow return type must be parameterized as Flow<Foo> or Flow<out Foo>" }
			val responseType = getParameterUpperBound(0, returnType)
			check(responseType is ParameterizedType) { "Response must be parameterized as Response<Foo> or Response<out Foo>" }
			val rawFlowType = getRawType(responseType)
			check(rawFlowType == NetworkState::class.java)
			return FlowNetworkStateCallAdapter<Any>(
				getParameterUpperBound(
					0,
					responseType
				),
				responseErrorParser
			)
		}
	}
}