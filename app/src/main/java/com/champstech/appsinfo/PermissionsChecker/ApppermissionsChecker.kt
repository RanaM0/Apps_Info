package com.champstech.appsinfo.PermissionsChecker

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Log
import kotlin.math.log

class ApppermissionsChecker(private val context: Context) {


    fun getPackagePermissions(packageName: String): List<String>? {

        try {

            val packagInfo: PackageInfo =
                context.packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
            val permissions = packagInfo.requestedPermissions

            if (permissions != null && permissions.isNotEmpty()) {
                Log.d("Permissions", "Permissions for $packageName")

                for (permission in permissions) {
                    Log.d("Permissions", permission)

                }
                return permissions.toList()
            } else {
                Log.d("Permissions", "No Permissions Requested By the $packageName")
            }
        } catch (e: PackageManager.NameNotFoundException) {

            e.printStackTrace()

        }
        return null
    }

}
