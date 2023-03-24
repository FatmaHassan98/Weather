package com.example.weather.home.view

import androidx.recyclerview.widget.DiffUtil
import com.example.weather.model.Hourly

class DiffUtilsHour : DiffUtil.ItemCallback<Hourly>() {
    override fun areItemsTheSame(oldItem: Hourly, newItem: Hourly): Boolean {
        return oldItem.dt == newItem.dt
    }

    override fun areContentsTheSame(oldItem: Hourly, newItem: Hourly): Boolean {
        return oldItem == newItem
    }
}