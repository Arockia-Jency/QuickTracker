package com.example.quicktasker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.quicktasker.data.ProfileStore
import com.example.quicktasker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ProfileStore.init(this)

        // 1. Navigation for standard tabs
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_tasks -> loadFragment(TasksFragment())
                R.id.nav_profile -> loadFragment(ProfileFragment())
            }
            true
        }

        // 2. FAB Click for "Create New Task" screen
        binding.fabAdd.setOnClickListener {
            loadFragment(AddFragment())
            // UX: Deselect tab icons when on the Add screen
            deselectAllTabs()
        }

        // Default start fragment
        if (savedInstanceState == null) {
            loadFragment(TasksFragment())
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            .replace(binding.fragmentContainer.id, fragment)
            .commit()
    }

    // Helper to clear BottomNav selection when FAB is clicked
    private fun deselectAllTabs() {
        binding.bottomNavigation.menu.setGroupCheckable(0, true, false)
        for (i in 0 until binding.bottomNavigation.menu.size()) {
            binding.bottomNavigation.menu.getItem(i).isChecked = false
        }
        binding.bottomNavigation.menu.setGroupCheckable(0, true, true)
    }

    // Call this from TaskAdapter to see specific task info
    fun openTaskDetails(taskId: Long) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            .replace(binding.fragmentContainer.id, TaskDetailsFragment.newInstance(taskId))
            .addToBackStack("task_details")
            .commit()
    }

    fun openEditProfile() {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            .replace(binding.fragmentContainer.id, EditProfileFragment())
            .addToBackStack("edit_profile")
            .commit()
    }

    fun selectTasksTab() {
        binding.bottomNavigation.selectedItemId = R.id.nav_tasks
    }
}