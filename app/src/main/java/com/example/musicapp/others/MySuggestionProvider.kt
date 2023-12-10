package com.example.musicapp.others

import android.content.SearchRecentSuggestionsProvider

class MySuggestionProvider : SearchRecentSuggestionsProvider() {
     var AUTHORITY = "com.example.MySuggestionProvider"
     var MODE = DATABASE_MODE_QUERIES

    fun MySuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE)
    }



}