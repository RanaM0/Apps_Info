package com.champstech.appsinfo.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.champstech.appsinfo.Fragments.ScanAppsFrag
import com.champstech.appsinfo.Models.BackupModel
import com.champstech.appsinfo.Models.ScanModel
import com.champstech.appsinfo.R


class ScanAdapter(private var dataList: List<ScanModel>, private val listener: ScanAppsFrag) :
    RecyclerView.Adapter<ScanAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val appImage = itemView.findViewById<ImageView>(R.id.ivitemsImage)
        val txtAppname = itemView.findViewById<TextView>(R.id.txtItemname)
        val txtAppPackagename = itemView.findViewById<TextView>(R.id.txtItemPackage)
        val cardforClick = itemView.findViewById<CardView>(R.id.cardrvItems)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScanAdapter.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_items_scan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScanAdapter.ViewHolder, position: Int) {
        val listItems = dataList[position]
        holder.txtAppname.text = listItems.appname
        holder.txtAppPackagename.text = listItems.appPackage
        holder.appImage.setImageDrawable(listItems.imgApp)

        holder.itemView.setOnClickListener {
            listener.onItemClick(listItems)

        }
    }

    override fun getItemCount(): Int = dataList.size

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

//    fun getFilter(): Filter {
//        return object : Filter() {
//            override fun performFiltering(constraint: CharSequence?): FilterResults {
//                val query = constraint.toString().toLowerCase()
//
//                val results = FilterResults()
//                val filteredList = dataList.filter {
//                    it.appname.toLowerCase().contains(query)
//                }
//
//                results.count = filteredList.size
//                results.values = ArrayList(filteredList) // Create a new ArrayList
//
//                return results
//            }
//
//            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
//                val filteredList = results?.values as List<ScanModel>? ?: emptyList()
//                dataList = ArrayList(filteredList)
//                notifyDataSetChanged()
//            }
//        }
//    }

    interface OnItemClickListener {
        fun onItemClick(data: ScanModel)
    }

}