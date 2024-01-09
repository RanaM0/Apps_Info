package com.champstech.appsinfo.Adapters

import android.util.Log
import android.util.MonthDisplayHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.champstech.appsinfo.Fragments.ApplistWithBackup
import com.champstech.appsinfo.Fragments.ApplistWithBackup.Companion.appListBackup
import com.champstech.appsinfo.Fragments.Backups.ShowBackUp.Companion.backupAppsList
import com.champstech.appsinfo.Models.BackupListModel
import com.champstech.appsinfo.Models.BackupModel
import com.champstech.appsinfo.Models.ScanModel
import com.champstech.appsinfo.R

class ApplistBackupAdapter(
    private var appBacupList: List<BackupListModel>,
    private val listener: ApplistWithBackup
) : RecyclerView.Adapter<ApplistBackupAdapter.ViewHolder>() {
    class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {
        val txtAppname = itemView.findViewById<TextView>(R.id.txtBackupname)
        val txtAppVersion = itemView.findViewById<TextView>(R.id.txtBackupVersion)
        val imglist = itemView.findViewById<ImageView>(R.id.imgBackupItems)
        val imgtoBackup = itemView.findViewById<ImageView>(R.id.ivBackupMoreOpt)
        val txtSize = itemView.findViewById<TextView>(R.id.txtBackuplistsize)
        val txtMbPlaceHolder = itemView.findViewById<TextView>(R.id.txtMbPlaceHolder)


    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.backup_list_items, parent, false)


        return ViewHolder(view)
    }

    private fun formatSize(size: Double): String {
        val mbSize = size / (1024.0 * 1024.0)
        return if (mbSize > (1024.0 * 1024.0)) {
            String.format("%.2f MB", mbSize / (1024.0 * 1024.0))
        } else {
            String.format("%.2f KB", mbSize)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listItem = appBacupList[position]

        holder.txtAppname.text = listItem.backupName
        holder.txtAppVersion.text = listItem.Backupversion
        holder.imgtoBackup.setImageResource(R.drawable.ic_backup2)
        holder.imglist.setImageDrawable(listItem.backupImg)
        holder.txtSize.visibility = View.VISIBLE
        holder.txtSize.text = formatSize(listItem.BackupSize)
        holder.txtMbPlaceHolder.visibility = View.VISIBLE

            //Check if the Backup list already contains the following app
            holder.imgtoBackup.setOnClickListener {
                listener.onItemClick(listItem)
        }

    }

    override fun getItemCount(): Int= appBacupList.size



    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    interface OnItemClickListener {
        fun onItemClick(data: BackupListModel)
    }


}