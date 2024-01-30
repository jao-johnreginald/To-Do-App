package com.johnreg.to_doapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // FragmentContainerView is not very friendly, you have to access it from the supportFragmentManager
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController

        /*
        This is how you put a back arrow on the action bar and make the title change
        depending on which fragment is in the FragmentContainerView

        1) Set up the action bar with navigation controller and pass the navController
        Cannot use 'findNavController(R.id.fragmentContainerView)' inside, it throws an Error
        Instead use the navController variable created, copied from StackOverflow
         */
        setupActionBarWithNavController(navController)
    }

    // 2) Override onSupportNavigateUp() and return navController.navigateUp()
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}