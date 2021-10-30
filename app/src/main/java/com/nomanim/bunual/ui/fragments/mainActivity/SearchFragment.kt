package com.nomanim.bunual.ui.fragments.mainActivity

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nomanim.bunual.databinding.FragmentSearchBinding
import com.paulrybitskyi.persistentsearchview.adapters.model.SuggestionItem
import com.paulrybitskyi.persistentsearchview.listeners.OnSuggestionChangeListener
import com.paulrybitskyi.persistentsearchview.utils.SuggestionCreationUtil
import com.paulrybitskyi.persistentsearchview.utils.VoiceRecognitionDelegate

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentSearchBinding.inflate(inflater,container,false)

        binding.searchView.isInputFocused

        with(binding.searchView) {
            setOnLeftBtnClickListener {
                // Handle the left button click
            }
            setOnClearInputBtnClickListener {
                // Handle the clear input button click
            }

            // Setting a delegate for the voice recognition input
            setVoiceRecognitionDelegate(VoiceRecognitionDelegate(requireActivity()))

            setOnSearchConfirmedListener { searchView, query ->
                // Handle a search confirmation. This is the place where you'd
                // want to save a new query and perform a search against your
                // data provider.
            }

            setOnSearchQueryChangeListener { searchView, oldQuery, newQuery ->
                // Handle a search query change. This is the place where you'd
                // want load new suggestions based on the newQuery parameter.
            }

            setOnSuggestionChangeListener(object : OnSuggestionChangeListener {

                override fun onSuggestionPicked(suggestion: SuggestionItem) {
                    // Handle a suggestion pick event. This is the place where you'd
                    // want to perform a search against your data provider.
                }

                override fun onSuggestionRemoved(suggestion: SuggestionItem) {
                    // Handle a suggestion remove event. This is the place where
                    // you'd want to remove the suggestion from your data provider.
                }

            })
        }
        return binding.root
    }

    /*override fun onResume() {
        super.onResume()

        val searchQueries = if(binding.searchView.isInputQueryEmpty) {

            mDataProvider.getInitialSearchQueries()
        } else {

            mDataProvider.getSuggestionsForQuery(binding.searchView.inputQuery)
        }

        // Converting them to recent suggestions and setting them to the widget
        binding.searchView.setSuggestions(SuggestionCreationUtil.asRecentSearchSuggestions(searchQueries), false)
    }*/

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Calling the voice recognition delegate to properly handle voice input results
        VoiceRecognitionDelegate.handleResult(binding.searchView, requestCode, resultCode, data)
    }

}