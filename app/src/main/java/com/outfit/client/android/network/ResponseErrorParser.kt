package com.outfit.client.android.network

import com.outfit.client.android.error.ProblemDetailsException
import com.outfit.client.android.error.ResponseStatusException
import com.outfit.client.android.error.ValidationProblemDetailsException
import com.squareup.moshi.Moshi
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject

class ResponseErrorParser @Inject constructor(
	moshi: Moshi
) {
	private val problemDetailsExceptionAdapter = moshi.adapter(ProblemDetailsException::class.java)
	private val validationProblemDetailsExceptionAdapter =
		moshi.adapter(ValidationProblemDetailsException::class.java)

	fun parseError(response: Response<*>): Exception =
		parseErrorInternal(response) ?: ResponseStatusException(response.code())


	private fun parseErrorInternal(response: Response<*>): Exception? {
		val contentType = response.headers()["content-type"]
		if (contentType == null) {
			Timber.w("content-type is null")
			return null
		}

		val matchResult = Regex("""application/(.+)?json""").find(contentType)
		if (matchResult == null) {
			Timber.w("response content type is not application/*+json")
			return null
		}

		val errorBody = response.errorBody()
		if (errorBody == null) {
			Timber.w("response content type is application/*+json, But errorBody is null")
			return null
		}

		return when (val value = matchResult.groups[1]?.value) {
			"problem+" -> problemDetailsExceptionAdapter.fromJson(errorBody.source())
			"validation.problem+" -> validationProblemDetailsExceptionAdapter.fromJson(errorBody.source())
			else -> {
				Timber.w("response content type is application/*+json, But subtype(${value}) isn't supported")
				null
			}
		}
	}
}