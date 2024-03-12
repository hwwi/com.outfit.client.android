package com.outfit.client.android.widget.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

abstract class AbstractBaseAdapter<T>(
	private val context: Context,
	private val layoutResourceId: Int,
	private var itemList: MutableList<T> = mutableListOf(),
	private var notifyOnChange: Boolean = true
) : BaseAdapter() {
	private val lock = Any()
	private val inflater: LayoutInflater = LayoutInflater.from(context)

	override fun notifyDataSetChanged() {
		super.notifyDataSetChanged()
		notifyOnChange = true
	}

	override fun getItem(position: Int): T = itemList[position]

	override fun getItemId(position: Int): Long = position.toLong()

	override fun getCount(): Int = itemList.size

	override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
		val holder = when (convertView) {
			null -> inflater.inflate(layoutResourceId, parent, false).let { view ->
				ViewHolderContainer(view).apply {
					view.tag = this
				}
			}

			else -> convertView.tag as ViewHolderContainer
		}

		onBindView(context, holder, getItem(position), position)

		return holder.containerView
	}

	abstract fun onBindView(context: Context, holder: ViewHolderContainer, item: T, position: Int)

	fun add(item: T, position: Int? = null) {
		synchronized(lock) {
			when (position) {
				null -> itemList.add(item)
				else -> itemList.add(position, item)
			}
		}
		if (notifyOnChange) notifyDataSetChanged()
	}

	fun addAll(items: Collection<T>) {
		synchronized(lock) {
			itemList.addAll(items)
		}
		if (notifyOnChange) notifyDataSetChanged()
	}

	fun addAll(vararg items: T) {
		synchronized(lock) {
			itemList.addAll(items)
		}
		if (notifyOnChange) notifyDataSetChanged()
	}

	fun remove(item: T) {
		synchronized(lock) {
			itemList.remove(item)
		}
		if (notifyOnChange) notifyDataSetChanged()
	}

	fun clear() {
		synchronized(lock) {
			itemList.clear()
		}
		if (notifyOnChange) notifyDataSetChanged()
	}
}