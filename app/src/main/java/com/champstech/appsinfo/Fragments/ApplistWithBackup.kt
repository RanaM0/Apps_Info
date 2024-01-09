package com.champstech.appsinfo.Fragments

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.champstech.appsinfo.Adapters.ApplistBackupAdapter
import com.champstech.appsinfo.Adapters.BackupAdapter
import com.champstech.appsinfo.Adapters.ScanAdapter
import com.champstech.appsinfo.Fragments.Backups.BackupUtil
import com.champstech.appsinfo.Models.BackupListModel
import com.champstech.appsinfo.Models.BackupModel
import com.champstech.appsinfo.Models.ScanModel
import com.champstech.appsinfo.databinding.FragmentApplistWithBackupBinding
import java.io.ByteArrayOutputStream
import java.io.File

class ApplistWithBackup : Fragment(), ApplistBackupAdapter.OnItemClickListener {

    private var _binding: FragmentApplistWithBackupBinding? = null
    private val binding get() = _binding!!

    companion object {
        val appListBackup = mutableListOf<BackupListModel>()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentApplistWithBackupBinding.inflate(inflater, container, false)


        getAppList()

        toolbarBackupList()

        return binding.root
    }

    private fun toolbarBackupList(){
        binding.toolbarApplist.txtToolbarTitle.text = "App Info : Store Info"
        binding.toolbarApplist.ivBackarrow.setOnClickListener {
            findNavController().popBackStack()
        }
    }


    private fun setUpRecycler() {
        val adapter = ApplistBackupAdapter(appListBackup, this@ApplistWithBackup)
        binding.rvApplistBackup.adapter = adapter
        binding.rvApplistBackup.layoutManager = LinearLayoutManager(requireContext())
        getProgress()
        adapter.notifyDataSetChanged()
    }

    private fun getProgress() {
        appListProgress(true)
        Handler(Looper.getMainLooper()).postDelayed({
            appListProgress(false)
        }, 100)
    }

    private fun appListProgress(show: Boolean) {
        if (show) {
            binding.progressAppList.visibility = View.VISIBLE
            binding.rvApplistBackup.visibility = View.GONE
        } else {
            binding.rvApplistBackup.visibility = View.VISIBLE
            binding.progressAppList.visibility = View.GONE
        }
    }

    private fun getAppList() {
        appListBackup.clear() // Clear the list to avoid data repetition

        val packageManager = requireContext().packageManager

        if (packageManager != null) {
            val intent = Intent(Intent.ACTION_MAIN, null)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)

            val resolveInfoList =
                packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

            for (resolveInfo in resolveInfoList) {
                if ((resolveInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0) {
                    try {
                        val iconInfo = packageManager.getApplicationInfo(resolveInfo.packageName, 0)
                        val icon = packageManager.getApplicationIcon(iconInfo)
                        val appName = resolveInfo.loadLabel(packageManager).toString()
                        val packageName = resolveInfo.packageName.toString()
                        val appPath = resolveInfo.sourceDir.toString()

                        // Calculate appSize in MB
                        val appbackupSize = File(appPath).length().toDouble()

                        // Get App version
                        val packageInfo = packageManager.getPackageInfo(packageName, 0)
                        val appVersion = packageInfo.versionName

                        val appInfo = BackupListModel(
                            icon,
                            appName,
                            packageName,
                            appbackupSize,
                            appVersion,
                            appPath
                        )

                        appListBackup.add(appInfo)
                    } catch (e: PackageManager.NameNotFoundException) {
                        e.printStackTrace()
                    }
                }
            }
        }
        setUpRecycler()
    }

    override fun onItemClick(data: BackupListModel) {

        createBackup(data.backupPkg)

    }

    private fun createBackup(packageName: String) {
        if (BackupUtil.backupApp(requireContext(), packageName)) {
            Toast.makeText(requireContext(), "App backed up successfully", Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(
                requireContext(),
                "Failed to create backup of the App",
                Toast.LENGTH_SHORT
            ).show()
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}