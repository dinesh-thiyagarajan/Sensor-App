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
import com.dineshworkspace.sensorapp.dataModels.SubscriptionStatus

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
                when (sensorItem.subscriptionStatus) {
                    SubscriptionStatus.NOT_YET -> sensorItem.subscriptionStatus =
                        SubscriptionStatus.TO_BE_SUBSCRIBED
                    SubscriptionStatus.SUBSCRIBED -> sensorItem.subscriptionStatus =
                        SubscriptionStatus.UN_SUBSCRIBED
                    SubscriptionStatus.UN_SUBSCRIBED -> sensorItem.subscriptionStatus =
                        SubscriptionStatus.TO_BE_SUBSCRIBED
                }
                updateUi(holder, sensorItem)
                sensorSelectedCallback.onSensorSelected(sensorItem)
            }
            holder.tvSensorName.text = sensorItem.sensorName
            updateUi(holder, sensorItem)
        }
    }

    private fun updateUi(holder: ViewHolder, sensorItem: Sensor) {
        holder.ivSensorSelection.visibility =
            if (sensorItem.subscriptionStatus == SubscriptionStatus.SUBSCRIBED
                || sensorItem.subscriptionStatus == SubscriptionStatus.TO_BE_SUBSCRIBED
            ) View.VISIBLE else View.GONE
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