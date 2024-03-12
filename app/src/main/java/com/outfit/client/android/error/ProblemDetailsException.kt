package com.outfit.client.android.error

import java.io.PrintStream
import java.io.PrintWriter


open class ProblemDetailsException(
	val status: Int,
	val title: String,
	val detail: String,
	val type: String,
	val instance: String?
) : Exception(title) {

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is ProblemDetailsException) return false

		if (status != other.status) return false
		if (title != other.title) return false
		if (detail != other.detail) return false
		if (type != other.type) return false
		if (instance != other.instance) return false

		return true
	}

	override fun hashCode(): Int {
		var result = status
		result = 31 * result + title.hashCode()
		result = 31 * result + detail.hashCode()
		result = 31 * result + type.hashCode()
		result = 31 * result + instance.hashCode()
		return result
	}

	override fun printStackTrace(s: PrintStream) {
		s.println(dummyStackTrace())
	}

	override fun printStackTrace(s: PrintWriter) {
		s.println(dummyStackTrace())
	}

	private fun dummyStackTrace(): String =
		"${javaClass.simpleName}-> status: $status, title: $title"
}

