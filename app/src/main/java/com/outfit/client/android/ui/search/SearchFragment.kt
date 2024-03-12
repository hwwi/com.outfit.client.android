package com.outfit.client.android.ui.search

import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.getSystemService
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.outfit.client.android.R
import com.outfit.client.android.databinding.FragmentSearchBinding
import com.outfit.client.android.extension.hideSoftInputFromWindow
import com.outfit.client.android.pref.IntroducePref
import com.outfit.client.android.widget.DataBindingFragment
import com.outfit.client.android.widget.SyntheticFragment
import com.outfit.client.android.widget.adapter.LoadingStateInterceptor
import com.outfit.client.android.widget.controller.SearchGetPayloadController
import javax.inject.Inject

class SearchFragment @Inject constructor(
) : DataBindingFragment<FragmentSearchBinding>(R.layout.fragment_search) {

	private val viewModel: SearchViewModel by viewModels { viewModelProviderFactory }

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding.buttonCancel.setOnClickListener {
			binding.editQuery.text = null
		}
		binding.editQuery.setOnFocusChangeListener { v, hasFocus ->
			binding.listSuggestion.visibility = when {
				hasFocus -> View.VISIBLE
				else -> View.GONE
			}
		}

		binding.editQuery.addTextChangedListener {
			val isTextNotEmpty = it?.isNotEmpty() ?: false
			binding.buttonCancel.visibility = when {
				isTextNotEmpty -> View.VISIBLE
				else -> View.INVISIBLE
			}
			viewModel.search(it?.toString() ?: "")
		}

		val controller = SearchGetPayloadController().apply {
			onPersonClickListener = {
				activity?.hideSoftInputFromWindow()
				SearchFragmentDirections
					.actionToClosetFragment(null, it.id)
					.navigate()
			}
			onHashTagClickListener = {
				activity?.hideSoftInputFromWindow()
				SearchFragmentDirections
					.actionSearchFragmentToViewHashTagFragment(it.tag)
					.navigate()
			}
			onItemTagClickListener = { brandCode, productCode ->
				activity?.hideSoftInputFromWindow()
				SearchFragmentDirections
					.actionSearchFragmentToViewItemTagFragment(
						brandCode,
						productCode
					)
					.navigate()
			}
		}
		val loadingStateInterceptor = LoadingStateInterceptor(controller)
		binding.listSuggestion.setController(controller)

		viewModel.searchPayload.observe(viewLifecycleOwner) { searchItemList ->
			controller.setData(searchItemList)
		}

		viewModel.searchResultState.observe(viewLifecycleOwner) {
			val state = it.getContentIfNotHandled() ?: return@observe
			loadingStateInterceptor.loadingState = state
		}

		if (!IntroducePref.isIntroduceSearchFragment) {
			MaterialAlertDialogBuilder(requireContext())
				.setMessage(R.string.introduce_search_fragment)
				.setPositiveButton(R.string.str_ok) { _, _ ->
					IntroducePref.isIntroduceSearchFragment = true
					requestFocusAtEditQuery()
				}
				.show()
		} else requestFocusAtEditQuery()
	}

	private fun requestFocusAtEditQuery() {
		binding.editQuery.requestFocus()
		context?.getSystemService<InputMethodManager>()
			?.showSoftInput(binding.editQuery, InputMethodManager.SHOW_IMPLICIT)

	}
}
