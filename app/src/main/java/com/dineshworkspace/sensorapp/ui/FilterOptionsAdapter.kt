package com.dineshworkspace.sensorapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dineshworkspace.sensorapp.R

class FilterOptionsAdapter :
    ListAdapter<String, FilterOptionsAdapter.ViewHolder>(StringDiffUtilCallback()) {

    lateinit var sensorSelectedCallback: SensorSelectedCallback

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_filter, parent, false)
        return ViewHolder(itemView)
    }

    fun setOnSensorSelectedListener(sensorSelectedCallback: SensorSelectedCallback) {
        this.sensorSelectedCallback = sensorSelectedCallback
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position != -1) {
            holder.tvSensorName.setText(getItem(position))

            holder.itemView.setOnClickListener {
                sensorSelectedCallback.onSensorSelected(getItem(position))
            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvSensorName: TextView = itemView.findViewById(R.id.tv_sensor_name)
    }
}

class StringDiffUtilCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem.equals(newItem)
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}

interface SensorSelectedCallback {
    fun onSensorSelected(sensor: String)
}