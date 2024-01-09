package com.champstech.appsinfo.Fragments

import android.app.AlertDialog
import android.net.wifi.WifiManager
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.champstech.appsinfo.R
import com.champstech.appsinfo.Utils.NetworkInfoUtil
import com.champstech.appsinfo.Utils.WifiDetails
import com.champstech.appsinfo.databinding.FragmentNetworkInfoBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class NetworkInfo : Fragment() {


    private var _binding: FragmentNetworkInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNetworkInfoBinding.inflate(inflater, container, false)

        binding.toolbarNetwork.txtToolbarTitle.text = "Network Info"


        networkDialog()

        binding.toolbarNetwork.ivBackarrow.setOnClickListener {

            findNavController().popBackStack()

        }

        return binding.root
    }

    private fun networkDialog() {

        val networBuilder = AlertDialog.Builder(requireContext())

        val networkInfoHelper = NetworkInfoUtil(requireContext())
        val networkType = networkInfoHelper.getNetworkDetails().connectionType

        if (networkInfoHelper.isNetworkAvailable()) {

            if (networkType == "WIFI") {
                binding.txtnetworkName.text = "  " + networkInfoHelper.getWifiDetails().wifiName
                MainScope().launch {
                    val ipAddress = networkInfoHelper.getExternalIPAddress()

                    binding.txtExternalIp.text = ipAddress
                    binding.txtExternalIp.inputType = InputType.TYPE_CLASS_NUMBER
                    binding.txtHost.text = ipAddress
                }
                binding.txtLocalHost.text = networkInfoHelper.getLocalIPAddress()
                binding.txtBssid.text = networkInfoHelper.getWifiDetails().bssid
                binding.txtMac.text = networkInfoHelper.getHostAndMacInfo().second
                binding.txtBroadcastAdrs.text = networkInfoHelper.getWifiDetails().broadcastAddress
                binding.txtSpeed.text =
                    networkInfoHelper.getWifiDetails().linkSpeed.toString() + " Mbp/s"
                binding.txtStrength.text =
                    networkInfoHelper.getWifiDetails().signalStrength.toString() + " dBm"
                binding.txtConnectionType.text =
                    networkInfoHelper.getNetworkDetails().connectionType
                binding.txtnetworkType.text =
                    networkInfoHelper.getNetworkDetails().networkType.toString()
                binding.txtRoaming.text = networkInfoHelper.getNetworkDetails().isRoaming.toString()
                binding.txtNetworkClass.text = networkInfoHelper.getNetworkDetails().networkClass

            } else {

                binding.operatorNameHeader.text = "Sim Operator: "
                binding.txtnetworkName.text =
                    " " + networkInfoHelper.getNetworkDetails().simOperator
                binding.llsimOperatorHeader.visibility = View.VISIBLE
                binding.txtsimClass.text = networkInfoHelper.getNetworkDetails().networkClass
                MainScope().launch {
                    val ipAddress = networkInfoHelper.getExternalIPAddress()

                    binding.txtExternalIp.text = ipAddress
                    binding.txtExternalIp.inputType = InputType.TYPE_CLASS_NUMBER
                    binding.txtHost.text = ipAddress
                }
                binding.txtLocalHost.text = networkInfoHelper.getLocalIPAddress()
                binding.txtBssid.text = networkInfoHelper.getWifiDetails().bssid
                binding.txtMac.text = networkInfoHelper.getHostAndMacInfo().second
                binding.txtBroadcastAdrs.text = networkInfoHelper.getWifiDetails().broadcastAddress
                binding.txtSpeed.text =
                    networkInfoHelper.getWifiDetails().linkSpeed.toString() + " Mbp/s"
                binding.txtStrength.text =
                    networkInfoHelper.getWifiDetails().signalStrength.toString() + " dBm"
                binding.txtConnectionType.text =
                    networkInfoHelper.getNetworkDetails().connectionType
                binding.txtnetworkType.text =
                    networkInfoHelper.getNetworkDetails().networkType.toString()
                binding.txtRoaming.text = networkInfoHelper.getNetworkDetails().isRoaming.toString()
                binding.txtNetworkClass.text = networkInfoHelper.getNetworkDetails().networkClass


            }
        } else {
            networBuilder.setTitle("Network Not Available")
            networBuilder.setMessage("Please connect to internet to access the network information")

            networBuilder.setPositiveButton("Connect") { it, _ ->

                networkInfoHelper.openNetworkSettings()

            }

            networBuilder.setNegativeButton("Cancel") { it, _ ->

                it.dismiss()
            }
            networBuilder.show()
        }

    }


}