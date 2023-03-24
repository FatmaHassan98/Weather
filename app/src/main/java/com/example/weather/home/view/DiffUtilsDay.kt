package com.example.weather.home.view

import androidx.recyclerview.widget.DiffUtil
import com.example.weather.model.Daily

class DiffUtilsDay: DiffUtil.ItemCallback<Daily>() {
    override fun areItemsTheSame(oldItem: Daily, newItem: Daily): Boolean {
        return oldItem.dt == newItem.dt
    }

    override fun areContentsTheSame(oldItem: Daily, newItem: Daily): Boolean {
        return oldItem == newItem
    }
}
