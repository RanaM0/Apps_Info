package com.champstech.appsinfo.Models

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import java.io.ByteArrayOutputStream

data class ScanModel(
    val imgApp: Drawable?,
    val appname: String,
    val appPackage: String,
    val appVersion: String,
    val appSDK: String,
    val appPath: String,
    val appSize: Double,
    val appInstalldate: String,
    val appUpdateDate: String
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Drawable::class.java.classLoader),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readDouble(),
        parcel.readString().toString(),
        parcel.readString().toString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByteArray(drawableToByteArray(imgApp))
        parcel.writeString(appname)
        parcel.writeString(appPackage)
        parcel.writeString(appVersion)
        parcel.writeString(appSDK)
        parcel.writeString(appPath)
        parcel.writeDouble(appSize)
        parcel.writeString(appInstalldate)
        parcel.writeString(appUpdateDate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ScanModel> {
        override fun createFromParcel(parcel: Parcel): ScanModel {
            return com.champstech.appsinfo.Models.ScanModel(parcel)
        }

        override fun newArray(size: Int): Array<ScanModel?> {
            return arrayOfNulls(size)
        }
    }

    private fun drawableToByteArray(drawable: Drawable?): ByteArray {
        return try {
            if (drawable == null) {
                // Handle the case when the drawable is null
                ByteArray(0)
            } else {
                val bitmap = (drawable as BitmapDrawable).bitmap
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                stream.toByteArray()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ByteArray(0)
        }
    }
}