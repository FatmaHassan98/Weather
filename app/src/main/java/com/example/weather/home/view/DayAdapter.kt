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
import com.example.weather.R
import com.example.weather.databinding.ItemDayBinding
import com.example.weather.model.pojos.Daily
import java.text.SimpleDateFormat
import java.util.*

class DayAdapter (private val context: Context) : ListAdapter<Daily, DayAdapter.ViewHolder>(DiffUtilsDay()){

    private lateinit var binding : ItemDayBinding


    inner class ViewHolder(var binding: ItemDayBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ItemDayBinding.inflate(inflater,parent,false)
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n", "ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.textDay.text = getDayFromTimestamp(getItem(position).dt)
        holder.binding.textTempDay.text =
            getItem(position).temp.max.toString()+" / "+getItem(position).temp.min
        holder.binding.textDescriptionDay.text = getItem(position).weather[0].description

        Glide.with(context)
        .load("https://openweathermap.org/img/wn/"+getItem(position).weather[0].icon+"@2x.png")
        .apply( RequestOptions().override(holder.binding.imageDay.width,holder.binding.imageDay.height))
        .into(holder.binding.imageDay)

    }

    @SuppressLint("SimpleDateFormat")
    private fun getDayFromTimestamp(timestamp: Long): String {
        val timeD = Date(timestamp * 1000)
        val sdf = SimpleDateFormat("E")
        return sdf.format(timeD)
    }

}

class DiffUtilsDay() : DiffUtil.ItemCallback<Daily>(){


    override fun areItemsTheSame(oldItem: Daily, newItem: Daily): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Daily, newItem: Daily): Boolean {
        return oldItem == newItem
    }

}