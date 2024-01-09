package com.champstech.appsinfo.Models

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import java.io.ByteArrayOutputStream

data class BackupListModel(

    val backupImg: Drawable?,
    val backupName: String?,
    val backupPkg:String,
    val BackupSize: Double,
    val Backupversion:String,
    val backupPath: String?
): Parcelable {
    constructor(parcel: Parcel) : this(
        backupImg = parcel.readParcelable(Drawable::class.java.classLoader),
        backupName = parcel.readString().toString(),
        backupPkg = parcel.readString().toString(),
        BackupSize = parcel.readDouble(),
        Backupversion = parcel.readString().toString(),
        backupPath = parcel.readString().toString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByteArray(drawableToByteArray(backupImg))
        parcel.writeString(backupName)
        parcel.writeString(backupPkg)
        parcel.writeDouble(BackupSize)
        parcel.writeString(Backupversion)
        parcel.writeString(backupPath)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BackupModel> {
        override fun createFromParcel(parcel: Parcel): BackupModel {
            return BackupModel(parcel)
        }

        override fun newArray(size: Int): Array<BackupModel?> {
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

    private fun byteArrayToDrawable(byteArray: ByteArray): Drawable? {
        return try {
            if (byteArray.isEmpty()) {
                // Handle the case when the byte array is empty
                null
            } else {
                val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                BitmapDrawable(bitmap)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
