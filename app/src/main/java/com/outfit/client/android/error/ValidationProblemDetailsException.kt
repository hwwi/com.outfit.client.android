package com.outfit.client.android.error

class ValidationProblemDetailsException(
	status: Int,
	title: String,
	detail: String,
	type: String,
	instance: String?,
	val errors: Map<String, List<String>>
) : ProblemDetailsException(status, title, detail, type, instance) {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is ValidationProblemDetailsException) return false
		if (!super.equals(other)) return false

		if (errors != other.errors) return false

		return true
	}

	override fun hashCode(): Int {
		var result = super.hashCode()
		result = 31 * result + errors.hashCode()
		return result
	}
}