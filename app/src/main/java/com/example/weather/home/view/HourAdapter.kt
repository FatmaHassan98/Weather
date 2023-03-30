package com.example.weather.home.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.weather.databinding.ItemTodayBinding
import com.example.weather.model.pojos.Hourly
import java.text.SimpleDateFormat
import java.util.*

class HourAdapter(private val context: Context) : ListAdapter<Hourly, HourAdapter.ViewHolder>(DiffUtilsHour()){

    private lateinit var binding : ItemTodayBinding
    inner class ViewHolder(var binding:ItemTodayBinding):RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ItemTodayBinding.inflate(inflater,parent,false)
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.textHour.text = getHourFromTimestamp(getItem(position).dt)

        holder.binding.textTempHour.text = getItem(position).temp.toString()

        Glide.with(context)
        .load(getItem(position).weather[0].icon)
        .apply( RequestOptions()
            .override(holder.binding.imageHour.width,holder.binding.imageHour.height))
        .into(holder.binding.imageHour)
    }
    @SuppressLint("SimpleDateFormat")
    private fun getHourFromTimestamp(timestamp: Long): String {
        val timeD = Date(timestamp * 1000)
        val sdf = SimpleDateFormat("h:mm aaa")
        return sdf.format(timeD)
    }
}

class DiffUtilsHour : DiffUtil.ItemCallback<Hourly>() {
    override fun areItemsTheSame(oldItem: Hourly, newItem: Hourly): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Hourly, newItem: Hourly): Boolean {
        return oldItem == newItem
    }
}