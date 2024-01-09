package com.champstech.appsinfo.Fragments.AppdetailFrags

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.drawable.toDrawable
import com.bumptech.glide.Glide
import com.champstech.appsinfo.R
import com.champstech.appsinfo.databinding.FragmentAppdetailBinding
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date


class Appdetail() : Fragment() {


    private lateinit var _binding: FragmentAppdetailBinding

    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): ConstraintLayout {
        // Inflate the layout for this fragment
        _binding = FragmentAppdetailBinding.inflate(inflater, container, false)


        getDatafromScan()

        return binding.root
    }


    private fun getDatafromScan() {

        val rcvdDrawable  = arguments?.getByteArray("Icon1")


        val bitmapAppDetail = rcvdDrawable?.let { byteArrayToBitmapAppDetail(it) }
        bitmapAppDetail?.let {
            Glide.with(requireContext())
                .load(it)
                .into(binding.ivDetailIcon)
        }


        binding.txtAppname.text = arguments?.getString("Name1")
        binding.txtAppVersion.text = arguments?.getString("Version1")
        binding.txtAppPkg.text = arguments?.getString("Pkg1")
        binding.txtSDK.text = arguments?.getString("SDK1")
        binding.txtAppPath.text = arguments?.getString("Path1")
        val size = arguments?.getDouble("Size1")
        val installdate = arguments?.getString("InstallDate1")
        val updatedate = arguments?.getString("UpdateDate1")


        binding.txtAppSize.text = size.toString()
        binding.txtAppInstallDate.text = installdate
            binding.txtAppUpdateDate.text = updatedate
    }

    private fun byteArrayToBitmapAppDetail(byteArray: ByteArray): Bitmap? {
        return try {
            BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


}