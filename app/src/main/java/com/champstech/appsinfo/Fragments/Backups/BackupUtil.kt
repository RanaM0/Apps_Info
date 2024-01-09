package com.champstech.appsinfo.Fragments.Backups

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import com.champstech.appsinfo.Models.BackupModel
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


object BackupUtil {

    fun backupApp(context: Context, packageName: String): Boolean {
        try {
            val packageManager = context.packageManager
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            val appDir = File(packageInfo.applicationInfo.sourceDir)

            val backupDir = File(Environment.getExternalStorageDirectory(), "AppBackups")
            if (!backupDir.exists()) {
                backupDir.mkdirs()
            }

            val backupFile = File(backupDir, "${packageInfo.packageName}.apk")

            val srcChannel = FileInputStream(appDir).channel
            val dstChannel = FileOutputStream(backupFile).channel

            dstChannel.transferFrom(srcChannel, 0, srcChannel.size())

            srcChannel.close()
            dstChannel.close()

            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    fun getBackupAppsList(context: Context): List<BackupModel> {
        val backupDir = File(Environment.getExternalStorageDirectory(), "AppBackups")
        val backupAppsList = mutableListOf<BackupModel>()

        backupDir.listFiles()?.let { files ->
            for (file in files) {
                val appPath = file.absolutePath.toString()
                val appname = file.name
                val appInfo = getAppInfo(context, appPath)

                backupAppsList.add(
                    BackupModel(
                        appInfo?.loadIcon(context.packageManager),
                        appInfo?.loadLabel(context.packageManager).toString(),
                        appname,
                        file.length(),
                        "",
                        appPath
                    )
                )
            }
        }

        return backupAppsList
    }

    private fun getAppInfo(context: Context, appPath: String): ApplicationInfo? {
        val packageManager = context.packageManager
        val packageInfo =
            packageManager.getPackageArchiveInfo(appPath, PackageManager.GET_ACTIVITIES)

        return packageInfo?.applicationInfo
    }

    fun restoreApp(context: Context, backupApp: BackupModel): Boolean {
        try {
            val apkFile = File(backupApp.file.toString())

            if (apkFile.exists()) {
                val intent = Intent(Intent.ACTION_VIEW)
                val apkUri: Uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    apkFile
                )

                intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

                context.startActivity(intent)
                return true
            } else {
                // Log an error or handle the case where the file doesn't exist.
                return false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Log the exception or handle it appropriately.
            return false
        }
}


    fun shareApp(context: Context, backupApp: BackupModel): Boolean {

        try {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "application/vnd.android.package-archive"

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                File(backupApp.file.toString())
            )

            intent.putExtra(Intent.EXTRA_STREAM, uri)
            intent.putExtra(Intent.EXTRA_TITLE, "${backupApp.Appname}")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            context.startActivity(Intent.createChooser(intent, "Share App Backup"))
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
}


    fun deleteAppBackup(backupApp: BackupModel): Boolean {
        try {
            val backupFile = File(backupApp.file.toString())
            if (backupFile.exists()) {
                return backupFile.delete()
            }
            return false
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }



}
