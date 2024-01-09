package com.champstech.appsinfo.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ActionMenuView.OnMenuItemClickListener
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.champstech.appsinfo.Models.BackupModel
import com.champstech.appsinfo.R


class BackupAdapter(
    private val backupList: List<BackupModel>,
    private val onMenuItemClickListener: (BackupModel, MenuItem) -> Unit
) :
    RecyclerView.Adapter<BackupAdapter.BViewHolder>() {


    class BViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val appIconImageView: ImageView = itemView.findViewById(R.id.imgBackupItems)
        val appmenuImageView: ImageView = itemView.findViewById(R.id.ivBackupMoreOpt)
        val appNameTextView: TextView = itemView.findViewById(R.id.txtBackupname)
        val appSizeTextView: TextView = itemView.findViewById(R.id.txtBackupVersion)


    }


    private fun formatSize(size: Long): String {
        val mbSize = size / (1024.0 * 1024.0)
        return if (mbSize > (1024.0 * 1024.0)) {
            String.format("%.2f MB", mbSize / (1024.0 * 1024.0))
        } else {
            String.format("%.2f KB", mbSize)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BackupAdapter.BViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.backup_list_items, parent, false)
        return BViewHolder(view)
    }

    override fun onBindViewHolder(holder: BackupAdapter.BViewHolder, position: Int) {
        val backupItems = backupList[position]
        holder.appNameTextView.text = backupItems.Appname
        holder.appSizeTextView.text = formatSize(backupItems.AppSize)+" MB"
        holder.appIconImageView.setImageDrawable(backupItems.ImgApp)

        holder.appmenuImageView.setOnClickListener {
            showPopupMenu(view = it, backupApp = backupItems)
        }

    }

    private fun showPopupMenu(view: View, backupApp: BackupModel) {
        val popupMenu = PopupMenu(view.context, view)
        val inflater: MenuInflater = popupMenu.menuInflater
        inflater.inflate(R.menu.backup_item_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item ->
            onMenuItemClickListener.invoke(backupApp, item)
            true


        }

        popupMenu.show()
    }


    override fun getItemCount(): Int {
        return backupList.size
    }
}
