package com.champstech.appsinfo.Fragments.Backups

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.DocumentsContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.champstech.appsinfo.Adapters.BackupAdapter
import com.champstech.appsinfo.Fragments.AppdetailFrags.AppActivity
import com.champstech.appsinfo.Models.BackupModel
import com.champstech.appsinfo.R
import com.champstech.appsinfo.databinding.FragmentShowBackUpBinding
import java.io.File
import java.io.FileOutputStream


class ShowBackUp : Fragment() {

    private var _binding: FragmentShowBackUpBinding? =null
    private val binding get() = _binding!!

    companion object{

        lateinit var backupAppsList: List<BackupModel>

    }

    private lateinit var backupAdapter: BackupAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentShowBackUpBinding.inflate(inflater, container, false)



        binding.toolbarBackup.ivPlaceholder.visibility = View.VISIBLE
        binding.toolbarBackup.ivPlaceholder.setImageResource(R.drawable.ic_toolbar_grid)
        binding.toolbarBackup.txtToolbarTitle.text = "App Backups"
        binding.toolbarBackup.ivBackarrow.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.toolbarBackup.ivPlaceholder.setOnClickListener {
            findNavController().navigate(R.id.action_showBackUp_to_applistWithBackup)
        }



        getBackupThread()


        return binding.root
    }


    private fun initializeRecycler(){

        backupAppsList = BackupUtil.getBackupAppsList(requireContext())

        backupAdapter = BackupAdapter(backupAppsList){ backupApp, menuItem ->
            when (menuItem.itemId) {
                R.id.btnmenuRestore -> {
                    // Handle restore action
                    // You can implement the logic to restore the app from the backup

                    if (BackupUtil.restoreApp(requireContext(), backupApp = backupApp)) {
                        Toast.makeText(requireContext(), "App restore initiated", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Failed to restore app", Toast.LENGTH_SHORT).show()
                    }

                }
                R.id.btnmenuShare -> {
                    // Handle share action
                    // You can implement the logic to share the backup file

                    if (BackupUtil.shareApp(requireContext(), backupApp)) {
                        Toast.makeText(requireContext(), "Sharing app initiated", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Failed to share app", Toast.LENGTH_SHORT).show()
                    }


                }
                R.id.btnmenuDelete -> {
                    // Handle delete action
                    // You can implement the logic to delete the backup file
                    if (BackupUtil.deleteAppBackup(backupApp)) {
//                        backupAppsList.remove(backupApp)

                        getBackupThread()
                        Toast.makeText(requireContext(), "App backup deleted", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Failed to delete app backup", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.rvBackup.adapter = backupAdapter
        binding.rvBackup.layoutManager = LinearLayoutManager(requireContext())

        if (backupAppsList.isEmpty()){

            binding.txtBackupBlank.visibility = View.VISIBLE

        }

    }
    private fun getBackupThread() {
        backupProgress(true)
        Handler(Looper.getMainLooper()).postDelayed({
            backupProgress(false)
            initializeRecycler()
            backupAdapter.notifyDataSetChanged()


        }, 3000)
    }

    private fun backupProgress(show: Boolean) {

        if (show) {

            binding.backupProgress.visibility = View.VISIBLE
            binding.rvBackup.visibility = View.GONE
        } else {

            binding.rvBackup.visibility = View.VISIBLE
            binding.backupProgress.visibility = View.GONE

        }


    }




}