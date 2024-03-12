package com.outfit.client.android.widget.adapter

import android.annotation.SuppressLint
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.outfit.client.android.data.vo.SupportedRegion
import java.util.*

@SuppressLint("SetTextI18n")
class PhoneNumberSupportedRegionAdapter : BaseAdapter() {
	val items: List<SupportedRegion>

	init {
		val phoneNumberUtil = PhoneNumberUtil.getInstance()

		items = phoneNumberUtil.supportedRegions.map {
				val locale = Locale("", it)
				val firstLetter = Character.codePointAt(locale.country, 0) - 0x41 + 0x1F1E6
				val secondLetter = Character.codePointAt(locale.country, 1) - 0x41 + 0x1F1E6
				SupportedRegion(
					it,
					phoneNumberUtil.getCountryCodeForRegion(it),
					locale.displayName,
					String(Character.toChars(firstLetter)) + String(Character.toChars(secondLetter))
				)
			}
			.sortedBy { it.displayName }
	}

	override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
		val v = convertView as? TextView
			?: createItemView(parent)
		val item = items[position]
		v.text = "${item.region} +${item.countryCode}"
		return v
	}

	override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
		val v = convertView as? TextView
			?: createItemView(parent)
		val item = items[position]
		v.text = "${item.flagEmoji} ${item.displayName} (+${item.countryCode})"
		return v
	}

	private fun createItemView(parent: ViewGroup): TextView =
		TextView(parent.context)
			.apply {
				val horizontal = TypedValue.applyDimension(
						TypedValue.COMPLEX_UNIT_DIP,
						4f,
						resources.displayMetrics
					)
					.toInt()
				val vertical = TypedValue.applyDimension(
						TypedValue.COMPLEX_UNIT_DIP,
						8f,
						resources.displayMetrics
					)
					.toInt()
				setPadding(horizontal, vertical, horizontal, vertical)
				textSize = 16f
				maxLines = 1
			}

	override fun getItem(position: Int): SupportedRegion = items[position]

	override fun getItemId(position: Int): Long = position.toLong()

	override fun getCount(): Int = items.size

}