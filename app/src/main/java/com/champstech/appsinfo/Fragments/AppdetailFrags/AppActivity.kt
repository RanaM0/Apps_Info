package com.champstech.appsinfo.Fragments.AppdetailFrags


import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.champstech.appsinfo.Fragments.Backups.BackupUtil
import com.champstech.appsinfo.Models.BackupModel

import com.champstech.appsinfo.R
import com.champstech.appsinfo.databinding.FragmentAppActivityBinding
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


class AppActivity : Fragment() {


    private var _binding: FragmentAppActivityBinding? = null

    private val binding get() = _binding!!

    private var activityDrawable: ByteArray? = null
    private lateinit var activityPackage: String
    private lateinit var activityDirectory: String
    private lateinit var activityAppname: String
    private var activityAppsize: Double = 0.0
    private lateinit var permissionList: List<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAppActivityBinding.inflate(inflater, container, false)


        getAppActivityData()


        return binding.root
    }

    private fun initializeView() {
        activityDrawable = arguments?.getByteArray("Icon2")

        val bitmapAppActivity = activityDrawable?.let { byteArrayToBitmapAppACtivity(it) }
        bitmapAppActivity?.let {
            Glide.with(requireContext())
                .load(it)
                .into(binding.ivAppactivityicon)
        }
        // Add other common view initialization code here
    }

    private fun getAppActivityData() {

        initializeView()

        activityPackage = arguments?.getString("Pkg2").toString()

        binding.btnCopylink.setOnClickListener {

            copyToClipboard(getAppLink())

            Toast.makeText(requireContext(), "Link Copied", Toast.LENGTH_SHORT).show()

        }

        binding.btnshareLink.setOnClickListener {

            shareLink(activityPackage)
        }


        binding.btnSearchPlay.setOnClickListener {

            openPlayStore(getAppLink())
        }

        binding.btnRequirepermission.setOnClickListener {
            permissionsDialoge()
        }

        binding.btnSaveToSDcard.setOnClickListener {

            saveToSDCard()
            Toast.makeText(requireContext(), "Backup Created Successfully", Toast.LENGTH_SHORT)
                .show()

        }


    }

    private fun getAppLink(): String {

        val packageName = activityPackage
        return "https://play.google.com/store/apps/details?id=$packageName"

    }

    private fun shareLink(activityPackage: String) {

        try {
            val sendIntent = Intent().apply {

                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Share App") // Optional subject
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Check out this awesome app! ${getAppLink()}$activityPackage"
                ) // Content to share
            }

            // Create a chooser to allow the user to pick the sharing app
            val shareIntent = Intent.createChooser(sendIntent, "Share Via")
            if (sendIntent.resolveActivity(requireActivity().packageManager) != null) {
                // Start the chosen sharing app
                startActivity(shareIntent)
            } else {
                // Handle the case where no apps can handle the share intent
                // (You can show a message or take other actions)
                Toast.makeText(
                    requireContext(),
                    "No app available to handle the share action",
                    Toast.LENGTH_SHORT
                ).show()
            }

        } catch (e: ActivityNotFoundException) {
            // Handle the case where the Play Store app is not installed
            Toast.makeText(requireContext(), "Play Store not installed", Toast.LENGTH_SHORT).show()
        }


    }

    private fun copyToClipboard(text: String) {
        val clipboard =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Link", text)
        clipboard.setPrimaryClip(clip)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun openPlayStore(link: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        startActivity(intent)
    }


    private fun getApppermissions(pkgName: String): List<String> {

        permissionList = arguments?.getStringArrayList("permission")!!

        val pkgManager: PackageManager = requireContext().packageManager

        try {
            val packageInfo = pkgManager.getPackageInfo(pkgName, PackageManager.GET_PERMISSIONS)
            val requestedPermissions = packageInfo.requestedPermissions

            if (requestedPermissions != null) {
                return requestedPermissions.toList()
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return emptyList()
    }

    private fun permissionsDialoge() {


        val permissionDialoge = Dialog(requireContext())
        permissionDialoge.setContentView(R.layout.permissions_dialogue)

        val txttoDisplayPermissions = permissionDialoge.findViewById<TextView>(R.id.txtPermissions)
        val btnPermssionOk = permissionDialoge.findViewById<ImageView>(R.id.btnPermissionOk)
        txttoDisplayPermissions.text = getApppermissions(activityPackage).joinToString("\n")

        permissionDialoge.setCanceledOnTouchOutside(false)

        btnPermssionOk.setOnClickListener {
            permissionDialoge.dismiss()
        }


        permissionDialoge.show()
    }


    private fun byteArrayToBitmapAppACtivity(byteArray: ByteArray): Bitmap? {
        return try {
            BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun saveToSDCard() {

        activityPackage = arguments?.getString("Pkg2").toString()
        activityDirectory = arguments?.getString("sdir").toString()

        if (BackupUtil.backupApp(requireContext(), activityPackage)) {
            Toast.makeText(requireContext(), "App backed up successfully", Toast.LENGTH_SHORT).show()
        }
        else{

            Toast.makeText(requireContext(), "Failed to Create Backupp of the App", Toast.LENGTH_SHORT).show()
        }

    }


}