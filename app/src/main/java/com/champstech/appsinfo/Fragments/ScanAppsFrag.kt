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
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.champstech.appsinfo.Adapters.ScanAdapter
import com.champstech.appsinfo.Models.ScanModel
import com.champstech.appsinfo.R
import com.champstech.appsinfo.databinding.FragmentScanAppsBinding
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ScanAppsFrag : Fragment(), ScanAdapter.OnItemClickListener {


    private var _binding: FragmentScanAppsBinding? = null

    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!
    val appList = mutableListOf<ScanModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentScanAppsBinding.inflate(inflater, container, false)


      getRecyclerData()

        binding.toolbarScan.ivBackarrow.setOnClickListener {
            onBackPressed()

        }

        return binding.root
    }

    private fun setupRecyclerView() {
        val filteredList = appList.filter {
            it.appname.toLowerCase().contains(binding.etSearchBar.text.toString().toLowerCase())
        }

        val adapter = ScanAdapter(filteredList, this@ScanAppsFrag)
        binding.rvScanApps.adapter = adapter
        binding.rvScanApps.layoutManager = GridLayoutManager(requireContext(), 2)
        getThread()
    }

    private fun getRecyclerData() {
        val packageManager = requireContext().packageManager

        if (packageManager != null) {
            val intent = Intent(Intent.ACTION_MAIN, null)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)

            val resolveInfoList = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

            for (resolveInfo in resolveInfoList) {
                if ((resolveInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0) {
                    try {
                        val iconInfo = packageManager.getApplicationInfo(resolveInfo.packageName, 0)
                        val icon = packageManager.getApplicationIcon(iconInfo)
                        val appName = resolveInfo.loadLabel(packageManager).toString()
                        val packageName = resolveInfo.packageName
                        val appSDK = Build.VERSION.SDK_INT.toString()
                        val appPath = resolveInfo.sourceDir

                        // Calculate appSize in MB
                        val appSize = File(appPath).length().toDouble() / (1024 * 1024)

                        // Get App version
                        val packageInfo = packageManager.getPackageInfo(packageName, 0)
                        val appVersion = packageInfo.versionName
                        val appInstallDate = packageInfo.firstInstallTime
                        val appUpdateDate = packageInfo.lastUpdateTime

                        // Format the install time
                        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                        val installDate = Date(appInstallDate)
                        val finalInstallDate = dateFormat.format(installDate)

                        // Format the update time
                        val updateDate = Date(appUpdateDate)
                        val finalUpdateDate = dateFormat.format(updateDate)
                        val appInfo = ScanModel(
                            icon,
                            appName,
                            packageName,
                            appVersion,
                            appSDK,
                            appPath,
                            appSize,
                            finalInstallDate,
                            finalUpdateDate
                        )

                        appList.add(appInfo)
                    } catch (e: PackageManager.NameNotFoundException) {
                        e.printStackTrace()
                    }
                }

            }

        }
        setupRecyclerView()
    }

    private fun getThread() {
        scanProgress(true)
        Handler(Looper.getMainLooper()).postDelayed({
            scanProgress(false)
            searchBarActions()
        }, 100)
    }

    private fun scanProgress(show: Boolean) {

        if (show) {

            binding.progressBarScan.visibility = View.VISIBLE
            binding.rvScanApps.visibility = View.GONE
        } else {

            binding.rvScanApps.visibility = View.VISIBLE
            binding.progressBarScan.visibility = View.GONE

        }


    }


    private fun searchBarActions() {

        binding.ivSearchbtnToggle.setImageResource(R.drawable.ic_toolbar_search)

        binding.etSearchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchQuery = s.toString().toLowerCase(Locale.getDefault())
                val filteredList = appList.filter {
                    it.appname.toLowerCase(Locale.getDefault()).contains(searchQuery)
                }

                val adapter = ScanAdapter(filteredList, this@ScanAppsFrag)
                binding.rvScanApps.adapter = adapter

                if (searchQuery.isEmpty()) {
                    binding.ivSearchbtnToggle.setImageResource(R.drawable.ic_toolbar_search)
                } else {
                    binding.ivSearchbtnToggle.setImageResource(R.drawable.ic_clear)
                    binding.ivSearchbtnToggle.setOnClickListener {
                        binding.etSearchBar.text!!.clear()
                    }
                }
        }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    override fun onItemClick(data: ScanModel) {

        val iconByteArray = drawableToByteArray(data.imgApp)

        val bundle = Bundle().apply {
            putByteArray("Icon", iconByteArray)
            putString("Name", data.appname)
            putString("Version", data.appVersion)
            putString("Pkg", data.appPackage)
            putString("SDK", data.appSDK)
            putString("Path", data.appPath)
            putDouble("Size", data.appSize)
            putString("InstallDate", data.appInstalldate)
            putString("UpdateDate", data.appUpdateDate)
        }


        val showinfoFrag = ShowappInfo()
        showinfoFrag.arguments = bundle

        findNavController().navigate(R.id.action_scanAppsFrag_to_showappInfo, bundle)

    }

    private fun drawableToByteArray(drawable: Drawable?): ByteArray {
        return try {
            if (drawable == null) {
                ByteArray(0)
            } else {
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    when (drawable) {
                        is BitmapDrawable -> drawable.bitmap
                        is AdaptiveIconDrawable -> {
                            val bitmapDrawable = BitmapDrawable(
                                resources,
                                Bitmap.createBitmap(
                                    drawable.intrinsicWidth,
                                    drawable.intrinsicHeight,
                                    Bitmap.Config.ARGB_8888
                                )
                            )
                            val canvas = Canvas(bitmapDrawable.bitmap)
                            drawable.setBounds(0, 0, canvas.width, canvas.height)
                            drawable.draw(canvas)
                            bitmapDrawable.bitmap
                        }
                        else -> throw IllegalArgumentException("Unsupported drawable type: ${drawable.javaClass}")
                    }
                } else {
                    TODO("VERSION.SDK_INT < O")
                }

                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                stream.toByteArray()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ByteArray(0)
        }
    }


    private fun onBackPressed() {
        findNavController().popBackStack()
    }

}