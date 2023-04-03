package com.example.weather.favorite.view

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.weather.database.room.entity.EntityFavorite
import com.example.weather.databinding.ItemFavoriteBinding
import java.util.*

class FavoriteAdapter(
    private val context: Context,
    private val favoriteClickLisener: FavoriteClickLisener,
) : ListAdapter<EntityFavorite, FavoriteAdapter.ViewHolder>(DiffUtilsFavorite()){

    private lateinit var binding : ItemFavoriteBinding

    inner class ViewHolder(var binding: ItemFavoriteBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ItemFavoriteBinding.inflate(inflater,parent,false)
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.cityFavorite.text = getLocationFromLatAndLon(
            getItem(position).lat,
            getItem(position).lon
        )

        holder.binding.temperatureFavorite.text =
            getItem(position).current?.temp.toString()

        holder.binding.statusFavorite.text = getItem(position).current?.weather?.get(0)?.main

        holder.binding.descriptionFavorite.text =
            getItem(position).current?.weather?.get(0)?.description

        Glide.with(context)
            .load("https://openweathermap.org/img/wn/"+
                    (getItem(position).current?.weather!![0]?.icon) +"@2x.png"

            )
            .apply(
                RequestOptions().override(
                    holder.binding.imageFavorite.width,
                    holder.binding.imageFavorite.height
                )
            )
            .into(holder.binding.imageFavorite)

        holder.binding.animationDelete.setOnClickListener {
            favoriteClickLisener.deleteFavorite(getItem(position))
        }

        holder.binding.cardFavorite.setOnClickListener {
            favoriteClickLisener.showData(getItem(position))
        }
    }

    private fun getLocationFromLatAndLon(lat: Double, lon: Double): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        val address = geocoder.getFromLocation(lat, lon, 1) as List<Address>
        return if (address.isNotEmpty()) {
            if (address[0].locality == null){
                "null"
            }else{
                address[0].locality
            }
        }else
            "null"    }
}

class DiffUtilsFavorite() : DiffUtil.ItemCallback<EntityFavorite>(){

    override fun areItemsTheSame(oldItem: EntityFavorite, newItem: EntityFavorite): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: EntityFavorite, newItem: EntityFavorite): Boolean {
        return oldItem == newItem
    }

}

