package com.dineshworkspace.sensorapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dineshworkspace.sensorapp.R
import com.dineshworkspace.sensorapp.dataModels.Sensor

class FilterOptionsAdapter :
    ListAdapter<Sensor, FilterOptionsAdapter.ViewHolder>(SensorDiffUtilCallback()) {

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
            val sensorItem = getItem(position)
            holder.itemView.setOnClickListener {
                sensorItem.isSelected = !sensorItem.isSelected
                sensorSelectedCallback.onSensorSelected(sensorItem)
                holder.ivSensorSelection.visibility =
                    if (sensorItem.isSelected) View.VISIBLE else View.GONE
            }

            holder.tvSensorName.text = sensorItem.sensorName
            holder.ivSensorSelection.visibility =
                if (sensorItem.isSelected) View.VISIBLE else View.GONE

        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvSensorName: TextView = itemView.findViewById(R.id.tv_sensor_name)
        var ivSensorSelection: ImageView = itemView.findViewById(R.id.iv_sensor_selection)
    }
}

class SensorDiffUtilCallback : DiffUtil.ItemCallback<Sensor>() {
    override fun areItemsTheSame(oldItem: Sensor, newItem: Sensor): Boolean {
        return oldItem.equals(newItem)
    }

    override fun areContentsTheSame(oldItem: Sensor, newItem: Sensor): Boolean {
        return oldItem.isSelected.equals(newItem.isSelected)
    }
}

interface SensorSelectedCallback {
    fun onSensorSelected(sensor: Sensor)
}