package com.outfit.client.android.extension

import android.text.Html
import android.text.Spanned
import androidx.annotation.IntDef
import androidx.core.text.HtmlCompat

@IntDef(
	value = [
		HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH,
		HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_HEADING,
		HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_LIST_ITEM,
		HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_LIST,
		HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_DIV,
		HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_BLOCKQUOTE,
		HtmlCompat.FROM_HTML_OPTION_USE_CSS_COLORS,
		HtmlCompat.FROM_HTML_MODE_COMPACT,
		HtmlCompat.FROM_HTML_MODE_LEGACY
	],
	flag = true
)
@Retention(AnnotationRetention.SOURCE)
internal annotation class FromHtmlFlags


@Deprecated("androidx ktx 쓸것.")
fun String.asHtml(
	//TODO IntDef not work at kotlin
	@FromHtmlFlags flags: Int = HtmlCompat.FROM_HTML_MODE_COMPACT,
	imageGetter: Html.ImageGetter? = null,
	tagHandler: Html.TagHandler? = null
): Spanned = HtmlCompat.fromHtml(
	this,
	flags,
	imageGetter,
	tagHandler
)
