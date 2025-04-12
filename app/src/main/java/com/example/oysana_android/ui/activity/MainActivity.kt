package com.example.oysana_android.ui.activity

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.oysana_android.R
import com.example.oysana_android.data.database.AuthManager
import com.example.oysana_android.data.network.ApiClient
import com.example.oysana_android.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ApiClient.init(applicationContext)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.nav_host_fragment)
        binding.bottomNavigation.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.mainFragment,
                R.id.trialCourseDetailsFragment -> {
                    binding.bottomNavigation.menu.findItem(R.id.mainFragment).isChecked = true
                }

                R.id.myCoursesFragment,
                R.id.courseDetailsFragment,
                R.id.courseTopicFragment,
                R.id.testFragment -> {
                    binding.bottomNavigation.menu.findItem(R.id.myCoursesFragment).isChecked = true
                }

                R.id.chatBotFragment -> {
                    binding.bottomNavigation.menu.findItem(R.id.chatBotFragment).isChecked = true
                }

                R.id.profileFragment,
                R.id.loginFragment -> {
                    binding.bottomNavigation.menu.findItem(R.id.profileFragment).isChecked = true
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            window.statusBarColor = Color.WHITE
            window.navigationBarColor = Color.WHITE
        }
    }
}
