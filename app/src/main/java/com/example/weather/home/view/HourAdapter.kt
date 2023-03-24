package com.example.weather.home.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.weather.databinding.ItemTodayBinding
import com.example.weather.model.Current

import com.example.weather.model.Hourly

class RecyclerAdapter(
    private val hours: List<Hourly>,
) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>(){

    private lateinit var binding : ItemTodayBinding


    inner class ViewHolder(var binding:ItemTodayBinding):RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ItemTodayBinding.inflate(inflater,parent,false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = hours.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.textToday.text = hours[position].dt.toString()
        holder.binding.textTempToday.text = hours[position].temp.toString()
    //        Glide.with(context)
//            .load(hours[position].thumbnail)
//            .apply( RequestOptions().override(holder.binding.imageRecycle.getWidth(),holder.binding.imageRecycle.getHeight()))
//            .into(holder.binding.imageRecycle)

    }

}