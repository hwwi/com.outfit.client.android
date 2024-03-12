package com.outfit.client.android.text.style

import android.graphics.Typeface
import android.text.Spannable
import android.text.Spanned
import android.text.TextPaint
import android.text.style.CharacterStyle
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.view.View
import java.util.regex.Matcher
import java.util.regex.Pattern

interface OnTagSpansClickListener {
	fun onPersonTagClick(name: String)
	fun onItemTagClick(brand: String, product: String?)
	fun onHashTagClick(tag: String)
}

fun String.setTagSpans(
	listener: OnTagSpansClickListener? = null,
	writerName: String? = null
): Spannable {
	val spannable = Spannable.Factory.getInstance().newSpannable(this)
	// writerName
	if (writerName != null) {
		val index = spannable.indexOf(writerName)
		if (index == 0)
			spannable.setSpan(
				StyleSpan(Typeface.BOLD),
				0,
				writerName.length,
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
			)
	}
	// personTag
	setSpan(
		spannable,
		Pattern.compile("(?:^|)@([a-zA-Z0-9_]+)(?:$|)")
	) {
		when (listener) {
			null -> DefaultSpan()
			else -> {
				val name = it.group(1)!!
				object : ClickableSpan() {
					override fun onClick(widget: View) {
						listener.onPersonTagClick(name)
					}

					override fun updateDrawState(ds: TextPaint) {
						ds.color = ds.linkColor
					}
				}
			}
		}
	}
	// itemTag
	setSpan(
		spannable,
		Pattern.compile("(?:^|)&([a-zA-Z0-9_-]+)(?:/([a-zA-Z0-9_-]+))?(?:\$|)")
	) {
		when (listener) {
			null -> DefaultSpan()
			else -> {
				val brand = it.group(1)!!
				val product = it.group(2)
				object : ClickableSpan() {
					override fun onClick(widget: View) {
						listener.onItemTagClick(brand, product)
					}

					override fun updateDrawState(ds: TextPaint) {
						ds.color = ds.linkColor
					}
				}
			}
		}
	}
	// hashTag
	setSpan(
		spannable,
		Pattern.compile("(?:^|)#([a-zA-Z가-힣0-9_]+)(?:\$|)")
	) {
		when (listener) {
			null -> DefaultSpan()
			else -> {
				val tag = it.group(1)!!
				object : ClickableSpan() {
					override fun onClick(widget: View) {
						listener.onHashTagClick(tag)
					}

					override fun updateDrawState(ds: TextPaint) {
						ds.color = ds.linkColor
					}
				}
			}
		}
	}
	return spannable
}

private fun setSpan(spannable: Spannable, pattern: Pattern, makeSpan: ((Matcher) -> Any)) {
	pattern.matcher(spannable).apply {
		while (find()) {
			spannable.setSpan(
				makeSpan(this),
				start(),
				end(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
			)
		}
	}
}

class DefaultSpan : CharacterStyle() {
	override fun updateDrawState(tp: TextPaint?) {
		if (tp != null)
			tp.color = tp.linkColor
	}
}
