package com.champstech.appsinfo.Fragments

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.core.view.contains
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.replace
import androidx.navigation.fragment.findNavController
import com.champstech.appsinfo.Fragments.AppdetailFrags.AppActivity
import com.champstech.appsinfo.Fragments.AppdetailFrags.Appdetail
import com.champstech.appsinfo.Models.ScanModel
import com.champstech.appsinfo.PermissionsChecker.ApppermissionsChecker
import com.champstech.appsinfo.R
import com.champstech.appsinfo.databinding.FragmentShowappInfoBinding
import java.util.ArrayList


class ShowappInfo : Fragment() {

    private var _binding: FragmentShowappInfoBinding? = null

    private val binding get() = _binding!!

    companion object {


        private lateinit var receivedDrawable: ByteArray
        private lateinit var name: String
        private lateinit var pkg: String
        private lateinit var version: String
        private lateinit var sdk: String
        private lateinit var path: String
        private lateinit var install: String
        private var size: Double? = null
        private lateinit var update: String
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentShowappInfoBinding.inflate(inflater, container, false)


        //Data of the scanned app
        getScanedData()

        //sending data to Appdetail Fragment
        sendAppdetailData()

        //sending Data to App Activity
        sendAppActivityData()


        binding.toolbarAppInfo.ivBackarrow.setOnClickListener {
            findNavController().popBackStack()

        }


        //set initial button stat
        showFrags()

        //onBackPressed Applied
        onBackPressed()

        //Btn app Launch Click Listeners
        binding.btnLaunchapp.setOnClickListener {

            //called launch app Function
            launchApp()
        }
        //Btn Check Update Click Listener
        binding.btnCheckUpdates.setOnClickListener {
            //Checking Update Function Called
            checkUpdate()

        }

        return binding.root
    }


    private fun showFrags() {

        binding.btnAppdetails.setOnClickListener {

            binding.AppactivityContainer.visibility = View.GONE
            binding.fragContainer.visibility = View.VISIBLE
            btnbgchange(binding.btnAppdetails)

        }

        binding.btnAppactivity.setOnClickListener {

            binding.fragContainer.visibility = View.GONE
            binding.AppactivityContainer.visibility = View.VISIBLE

            btnbgchange(binding.btnAppactivity)
        }

    }

    private fun btnbgchange(clickedButton: Button) {


        val selectedButton: Button? = if (binding.btnAppdetails == clickedButton) {
            binding.btnAppactivity
        } else {
            binding.btnAppdetails
        }

        // Change background and text color for clicked button
        clickedButton.setBackgroundResource(R.drawable.bg_app_detail_buttons)
        clickedButton.setTextColor(Color.WHITE) // Change to your desired text color

        // Reset background and text color for the selected button
        selectedButton?.setBackgroundResource(R.drawable.bg_app_detail_btn_toggle)
        selectedButton?.setTextColor(Color.BLACK) // Change to your desired text color

    }

    private fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {

            findNavController().popBackStack()
        }
    }


    private fun getScanedData() {

        receivedDrawable = arguments?.getByteArray("Icon")!!
        name = arguments?.getString("Name").toString()
        pkg = arguments?.getString("Pkg").toString()
        version = arguments?.getString("Version").toString()
        sdk = arguments?.getString("SDK").toString()
        path = arguments?.getString("Path").toString()
        size = arguments?.getDouble("Size")
        install = arguments?.getString("InstallDate").toString()
        update = arguments?.getString("UpdateDate").toString()

    }

    private fun sendAppdetailData() {

        val bundle = Bundle()


        bundle.putByteArray("Icon1", receivedDrawable)
        bundle.putString("Name1", name)
        bundle.putString("Version1", version)
        bundle.putString("Pkg1", pkg)
        bundle.putString("SDK1", sdk)
        bundle.putString("Path1", path)
        bundle.putDouble("Size1", size!!.toDouble() / (1024 * 1024))
        bundle.putString("InstallDate1", install)
        bundle.putString("UpdateDate1", update)

        val appdetailFrag = Appdetail()
        appdetailFrag.arguments = bundle

        childFragmentManager.beginTransaction().add(R.id.fragContainer, appdetailFrag)
            .addToBackStack(null).commit()

    }

    private fun launchApp() {

        pkg = arguments?.getString("Pkg").toString()

        val launchIntent: Intent? = requireActivity().packageManager.getLaunchIntentForPackage(pkg)
        if (launchIntent != null) {
            startActivity(launchIntent)
        } else {
            // Handle the case where the app cannot be launched
            Toast.makeText(requireContext(), "App cannot be launched", Toast.LENGTH_SHORT).show()
        }

    }


    // Function to get the installed version of the app
    private fun getAppVersion(packageName: String): Int {
        return try {
            val packageInfo = requireContext().packageManager.getPackageInfo(packageName, 0)
            packageInfo.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            // Handle the case where the app is not installed
            0
        }
    }

    private fun checkUpdate() {

        val installedVersion = getAppVersion(pkg)
        val latestversion = getLatestVersionFromServer(pkg)


        Toast.makeText(requireContext(), "Checking For Updates", Toast.LENGTH_SHORT).show()


        if (installedVersion < latestversion) {

            Toast.makeText(requireContext(), "Update Available", Toast.LENGTH_SHORT).show()

            openPlayStoreForUpdate(pkg)

        } else {
            Toast.makeText(
                requireContext(),
                "you are using tha Latest version of this App",
                Toast.LENGTH_SHORT
            ).show()

        }


    }

    // Function to simulate getting the latest version from a server
    private fun getLatestVersionFromServer(packageName: String): Int {
        // Replace this with your actual logic to get the latest version from a server
        // For simplicity, I'm returning a hardcoded value here
        return 2
    }


    // Function to open the Play Store page for the app
    private fun openPlayStoreForUpdate(packageName: String) {
        try {
            val intent = Intent(
                Intent.ACTION_SEND,
                Uri.parse("market://details?id=$packageName")
            )
            intent.setPackage("com.android.vending")
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // Handle the case where the Play Store app is not installed
            Toast.makeText(requireContext(), "Play Store not installed", Toast.LENGTH_SHORT).show()
        }

    }


    private fun sendAppActivityData() {

        val bundleAppActivity = Bundle()

        val appPermissions = ApppermissionsChecker(requireContext())

        val permission = appPermissions.getPackagePermissions(pkg)

        bundleAppActivity.putByteArray("Icon2", receivedDrawable)
        bundleAppActivity.putString("Pkg2", pkg)
        bundleAppActivity.putString("sdir",path)
        bundleAppActivity.putString("activityName", name)
        bundleAppActivity.putStringArrayList("permission", ArrayList(permission))

        val appActivity = AppActivity()

        appActivity.arguments = bundleAppActivity

        childFragmentManager.beginTransaction().add(R.id.AppactivityContainer, appActivity)
            .addToBackStack(null).commit()
    }


}