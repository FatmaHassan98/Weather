package com.example.weather.alerts.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.location.Address
import android.location.Geocoder
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.weather.R
import com.example.weather.database.room.entity.EntityAlert
import com.example.weather.database.room.entity.EntityFavorite
import com.example.weather.databinding.ItemAlertBinding
import com.example.weather.databinding.ItemFavoriteBinding
import com.example.weather.favorite.view.FavoriteClickLisener
import java.util.*

class AlertAdapter(private val context: Context ,
                   private val alertOnClicklistener: AlertOnClicklistener
) : ListAdapter<EntityAlert, AlertAdapter.ViewHolder>(DiffUtilsAlert()){

    private lateinit var binding: ItemAlertBinding

    inner class ViewHolder(var binding: ItemAlertBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ItemAlertBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        binding.cityAlert.text = getLocationFromLatAndLon(getItem(position).lat,getItem(position).lon)
        binding.startTimeAlertValue.text = getItem(position).start
        binding.endTimeAlertValue.text = getItem(position).end
        if (getItem(position).notification == "notification"){
            binding.animationAlert.setAnimation(R.raw.alert)
        }else{
            binding.animationAlert.setAnimation(R.raw.notification)
        }

        binding.btnDismiss.setOnClickListener {
            alertOnClicklistener.deleteAlert(getItem(position))
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

class DiffUtilsAlert() : DiffUtil.ItemCallback<EntityAlert>(){

    override fun areItemsTheSame(oldItem: EntityAlert, newItem: EntityAlert): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: EntityAlert, newItem: EntityAlert): Boolean {
        return oldItem == newItem
    }

}
