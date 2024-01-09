package com.champstech.appsinfo.Fragments

import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.champstech.appsinfo.R
import com.champstech.appsinfo.Utils.NetworkInfoUtil
import com.champstech.appsinfo.databinding.FragmentDashboardBinding


class DashboardFrag : Fragment() {


    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!

    private lateinit var PurchaseDialogue: Dialog

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)




        binding.toolbarDash.ivtoolbarInfo.setOnClickListener {

            findNavController().navigate(R.id.action_dashboardFrag_to_settingsFrag)

        }


        binding.cardScanBtn.setOnClickListener {

           requestAllFilesAccessPermission()

        }

        binding.cardBackupBtn.setOnClickListener {

            findNavController().navigate(R.id.action_dashboardFrag_to_showBackUp)
        }

        binding.cardNetworkBtn.setOnClickListener{
            findNavController().navigate(R.id.action_dashboardFrag_to_networkInfo)

        }


        onPurchaseButtonClick()


        onBackPressed()


        return binding.root
    }

    private fun onPurchaseButtonClick() {


        val networkInfo = NetworkInfoUtil(requireContext())

        if (networkInfo.isNetworkAvailable()){
            binding.toolbarDash.ivtoolbarAdblock.visibility = View.VISIBLE
            binding.toolbarDash.ivtoolbarAdblock.setOnClickListener {

                PurchaseDialogue = Dialog(requireContext())
                PurchaseDialogue.setContentView(R.layout.purchase_dialogue)

                val btnPurchase = PurchaseDialogue.findViewById<Button>(R.id.btnPurchase)
                val btnPurchaseCancel = PurchaseDialogue.findViewById<Button>(R.id.btnPurchaseCancel)

                PurchaseDialogue.setCanceledOnTouchOutside(false)

                btnPurchase.setOnClickListener {

                    Toast.makeText(requireContext(), "Purchase Successful......", Toast.LENGTH_SHORT).show()
                    PurchaseDialogue.dismiss()
                }

                btnPurchaseCancel.setOnClickListener {

                    Toast.makeText(
                        requireContext(),
                        "Canceled the Purchase successfully......",
                        Toast.LENGTH_SHORT
                    ).show()
                    PurchaseDialogue.dismiss()
                }

                PurchaseDialogue.show()

            }

        }

    }
    fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finish()
        }
    }


    @RequiresApi(Build.VERSION_CODES.R)
    private fun requestAllFilesAccessPermission() {
        if (!Environment.isExternalStorageManager()) {
            try {
                val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                } else {
                    // This block will not be reached because the @RequiresApi annotation
                    // ensures that this method is called only on Android R and later.
                    throw RuntimeException("Unsupported Android version")
                }

                intent.addCategory("android.intent.category.DEFAULT")
                intent.data = Uri.parse("package:com.champstech.appsinfo")

                // Start the activity for result
                requireContext().startActivity(intent)
            } catch (e: Exception) {
                // Handle exceptions if needed
                e.printStackTrace()
            }
        } else {
            openDashBoard()
        }
    }

    private fun openDashBoard() {
        findNavController().navigate(R.id.action_dashboardFrag_to_scanAppsFrag)
    }


}
