package com.example.musicapp.fragments

import android.app.SearchManager
import android.content.Intent
import android.content.SearchRecentSuggestionsProvider
import android.os.Bundle
import android.provider.SearchRecentSuggestions
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.example.musicapp.R
import com.example.musicapp.others.MySuggestionProvider


class SearchFragment(): Fragment(R.layout.fragment_search) {

    lateinit var searchView : SearchView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?


    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        searchView = view.findViewById(R.id.edt_search)
//        setHasOptionsMenu(true)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                Toast.makeText(activity,query,Toast.LENGTH_SHORT).show()
                return false
            }
        })
        return view
    }


}