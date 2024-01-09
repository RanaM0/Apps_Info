package com.champstech.appsinfo.Utils

import android.Manifest
import android.content.Context
import android.content.Context.WIFI_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.DhcpInfo
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.InetAddress
import java.net.URL
import java.net.UnknownHostException
import java.util.Locale
import java.util.Scanner

class NetworkInfoUtil(private val context:Context) {

    companion object {
        const val TAG = "NetworkUtils"
    }

    fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val activeNetwork =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false

            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                // for other devices which are able to connect with Ethernet
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting
        }
    }
    fun openNetworkSettings() {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Intent(Settings.ACTION_WIFI_SETTINGS)
        } else {
            TODO("VERSION.SDK_INT < R")
        }
        context.startActivity(intent)
    }


    suspend fun getExternalIPAddress(): String = withContext(Dispatchers.IO) {
        try {
            val url = URL("https://api.ipify.org?format=json")
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            connection.requestMethod = "GET"
            connection.connect()

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val scanner = Scanner(connection.inputStream)
                val response = if (scanner.hasNext()) scanner.next() else ""
                scanner.close()

                // Parse JSON and extract the IP address
                val jsonObject = JSONObject(response)
                val ipAddress = jsonObject.getString("ip")

                return@withContext ipAddress
            } else {
                "Failed to fetch external IP"
            }
        } catch (e: IOException) {
            "Failed to fetch external IP: ${e.message}"
        } catch (e: JSONException) {
            "Failed to parse JSON: ${e.message}"
        }
    }

    fun getLocalIPAddress(): String {
        try {
            val wifiManager =
                context.applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
            val wifiInfo: WifiInfo = wifiManager.connectionInfo
            val ipAddress: Int = wifiInfo.ipAddress
            return String.format(
                Locale.getDefault(),
                "%d.%d.%d.%d",
                ipAddress and 0xff,
                ipAddress shr 8 and 0xff,
                ipAddress shr 16 and 0xff,
                ipAddress shr 24 and 0xff
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get local IP address: ${e.message}")
        }
        return ""
    }


    fun getHostAndMacInfo(): Pair<String, String> {
        try {
            val wifiManager = context.applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
            val wifiInfo: WifiInfo = wifiManager.connectionInfo

            // Note: Getting host name might not always work as expected on Android.
            // Consider handling network-related exceptions appropriately.
            val hostName: String = try {
                InetAddress.getLocalHost().hostName
            } catch (e: UnknownHostException) {
                Log.e(TAG, "Failed to get host name: ${e.message}")
                ""
            }

            // Note: Retrieving MAC address is deprecated in Android 10 and above.
            // It's recommended to use alternative methods for unique identifiers.
            val macAddress: String = wifiInfo.macAddress ?: ""

            return Pair(hostName, macAddress)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get host and MAC address: ${e.message}")
        }
        return Pair("", "")
    }


    fun getWifiDetails(): WifiDetails {
        try {
            val wifiManager =
                context.applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
            val wifiInfo: WifiInfo = wifiManager.connectionInfo
            val dhcpInfo: DhcpInfo = wifiManager.dhcpInfo

            val wifiName =  wifiInfo.ssid
            val bssid =  wifiInfo.bssid
            val broadcastAddress = InetAddress.getByAddress(intToByteArray(dhcpInfo.ipAddress or (dhcpInfo.netmask.inv())))
                .hostAddress
            val linkSpeed = wifiInfo.linkSpeed
            val signalStrength =  wifiManager.connectionInfo.rssi

            return WifiDetails(wifiName, bssid, broadcastAddress, linkSpeed, signalStrength)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get WiFi details: ${e.message}")
        }
        return WifiDetails("", "", "", 0, 0)
    }

    fun getNetworkDetails(): NetworkDetails {
        try {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo

            val connectionType = networkInfo?.typeName ?: ""
            val networkClass = getNetworkClass(context)

            val telephonyManager =
                context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val simOperator = telephonyManager.simOperatorName
            val networkType = getNetworkType(context)
            val isRoaming = telephonyManager.isNetworkRoaming

            return NetworkDetails(
                connectionType,
                networkClass,
                simOperator,
                networkType,
                isRoaming
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get network details: ${e.message}")
        }
        return NetworkDetails("", "", "", 0, false)
    }

    private fun getNetworkClass(context: Context): String {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo

        return when (networkInfo?.type) {
            ConnectivityManager.TYPE_WIFI -> "Wi-Fi"
            ConnectivityManager.TYPE_MOBILE -> {
                when (networkInfo.subtype) {
                    TelephonyManager.NETWORK_TYPE_1xRTT,
                    TelephonyManager.NETWORK_TYPE_CDMA,
                    TelephonyManager.NETWORK_TYPE_EDGE,
                    TelephonyManager.NETWORK_TYPE_GPRS,
                    TelephonyManager.NETWORK_TYPE_IDEN -> "2G"
                    TelephonyManager.NETWORK_TYPE_EHRPD,
                    TelephonyManager.NETWORK_TYPE_EVDO_0,
                    TelephonyManager.NETWORK_TYPE_EVDO_A,
                    TelephonyManager.NETWORK_TYPE_EVDO_B,
                    TelephonyManager.NETWORK_TYPE_HSDPA,
                    TelephonyManager.NETWORK_TYPE_HSPA,
                    TelephonyManager.NETWORK_TYPE_HSPAP,
                    TelephonyManager.NETWORK_TYPE_HSUPA,
                    TelephonyManager.NETWORK_TYPE_UMTS -> "3G"
                    TelephonyManager.NETWORK_TYPE_LTE -> "4G"
                    else -> "Unknown"
                }
            }
            else -> "Unknown"
        }
    }

    private fun getNetworkType(context: Context): Int {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Handle the case where permission is not granted.
            // You may consider requesting the permission here.
            // For example:
            // ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.READ_PHONE_STATE), requestCode)
            // You can't return a meaningful network type here because the permission is not granted.
            // You may want to return a default value or handle it in some way.
            -1 // Example default value, replace it with what makes sense in your context.
        } else {
            telephonyManager.networkType
        }
    }

    private fun intToByteArray(value: Int): ByteArray {
        val byteArray = ByteArray(4)
        byteArray[0] = (value and 0xFF).toByte()
        byteArray[1] = (value shr 8 and 0xFF).toByte()
        byteArray[2] = (value shr 16 and 0xFF).toByte()
        byteArray[3] = (value shr 24 and 0xFF).toByte()
        return byteArray
    }

}

data class WifiDetails(
    val wifiName: String,
    val bssid: String,
    val broadcastAddress: String,
    val linkSpeed: Int,
    val signalStrength: Int
)

data class NetworkDetails(
    val connectionType: String,
    val networkClass: String,
    val simOperator: String,
    val networkType: Int,
    val isRoaming: Boolean
)


