package com.pollinator

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class PlantListAdapter(list : ArrayList<PlantBean>) : RecyclerView.Adapter<PlantListAdapter.PlantViewHolder>(){
    private var mData : ArrayList<PlantBean> = list
    interface OnItemClickListener{
        fun onItemClick(view : View, position : Int)
    }

    private lateinit var onItemClickListener : OnItemClickListener
    fun setOnItemClickListener(onItemClickListener: OnItemClickListener){
        this.onItemClickListener=onItemClickListener
    }

    inner class PlantViewHolder(item : View) : RecyclerView.ViewHolder(item) {
        var dateTv : TextView
        var timeTv : TextView
        var familyTv : TextView
        var genusTv : TextView
        var speciesTv : TextView
        init{
            dateTv = item.findViewById(R.id.item_date)
            timeTv = item.findViewById(R.id.item_time)
            familyTv = item.findViewById(R.id.item_family)
            genusTv = item.findViewById(R.id.item_genus)
            speciesTv = item.findViewById(R.id.item_species)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_plant ,parent,false)
        return PlantViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {

        holder.dateTv.text = mData[position].date
        holder.timeTv.text = mData[position].time
        holder.familyTv.text = mData[position].family
        holder.genusTv.text = mData[position].genus
        holder.speciesTv.text = mData[position].species
    }

    override fun getItemCount(): Int {
        return mData.size
    }

}