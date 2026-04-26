package com.example.quicktasker.data

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object ProfileStore {
    private const val PREFS_NAME = "quicktasker_profile"
    private const val KEY_NAME = "name"
    private const val KEY_TITLE = "title"
    private const val KEY_PHOTO_URI = "photo_uri"

    private lateinit var prefs: SharedPreferences

    private val _profile = MutableLiveData<UserProfile>()
    val profile: LiveData<UserProfile> = _profile

    fun init(context: Context) {
        if (::prefs.isInitialized) return
        prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val name = prefs.getString(KEY_NAME, null) ?: "Alex Harrison"
        val title = prefs.getString(KEY_TITLE, null) ?: "Senior Project Coordinator"
        val photoUri = prefs.getString(KEY_PHOTO_URI, null)

        _profile.value = UserProfile(name = name, title = title, photoUri = photoUri)
    }

    fun update(name: String, title: String, photoUri: String?) {
        check(::prefs.isInitialized) { "ProfileStore.init(context) must be called first." }

        val trimmedName = name.trim()
        val trimmedTitle = title.trim()

        prefs.edit()
            .putString(KEY_NAME, trimmedName)
            .putString(KEY_TITLE, trimmedTitle)
            .putString(KEY_PHOTO_URI, photoUri)
            .apply()

        _profile.value = UserProfile(name = trimmedName, title = trimmedTitle, photoUri = photoUri)
    }
}

