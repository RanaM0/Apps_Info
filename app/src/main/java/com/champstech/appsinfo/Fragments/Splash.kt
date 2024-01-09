package com.champstech.appsinfo.Fragments

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.activity.addCallback
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.champstech.appsinfo.R
import com.champstech.appsinfo.databinding.FragmentSplashBinding


class Splash : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!

    val navController = NavController
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding =  FragmentSplashBinding.inflate(inflater, container, false)


        // Show progress bar
        binding.progressBar.visibility = View.VISIBLE

        // Use a Handler to delay the transition to the next screen
        Handler().postDelayed({

            binding.progressBar.visibility = View.GONE


            navigateToNextScreen()


        },1000)

        return binding.root
    }

    private fun navigateToNextScreen() {
        // Use Navigation Component or any other navigation method to go to the main screen

       findNavController().navigate(R.id.action_splash_to_dashboardFrag)

    }


}