package com.example.weather.view

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.RadioButton
import android.widget.Switch
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.airbnb.lottie.LottieAnimationView
import com.example.weather.home.model.GPSLocation
import com.example.weather.R
import com.example.weather.database.room.entity.EntityAlert
import com.example.weather.database.shared.prefernces.SharedPreferenceSource
import com.example.weather.databinding.ActivityMainBinding
import com.example.weather.home.model.LocationStatus
import com.example.weather.map.view.MapsActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

const val PERMISSION_ID = 5005
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var  navController :NavController
    private lateinit var  navView: BottomNavigationView

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if (SharedPreferenceSource.getInstance(this).getSavedLocationWay() == ""){
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.start_dialog)
            val locationGps = dialog.findViewById(R.id.initialGps) as RadioButton
            val locationMap = dialog.findViewById(R.id.initialMap) as RadioButton
            val notification = dialog.findViewById(R.id.initialNotification) as Switch
            val save = dialog.findViewById(R.id.initialSave) as Button

            save.setOnClickListener {
                if ((locationGps.isChecked || locationMap.isChecked) && notification.isChecked){
                   if (locationGps.isChecked){
                       SharedPreferenceSource.getInstance(this).setLocationWay("GPS")
                   }else{
                       SharedPreferenceSource.getInstance(this).setLocationWay("Map")
                       SharedPreferenceSource.getInstance(this).setMap("Home")
                       val intent = Intent(this, MapsActivity::class.java)
                       startActivity(intent)
                   }
                    if (notification.isActivated){
                        SharedPreferenceSource.getInstance(this).setNotification("enable")
                    }else{
                        SharedPreferenceSource.getInstance(this).setNotification("disable")
                    }
                    dialog.dismiss()
                }else{
                    Snackbar.make(binding.root,"Please, Enter all data",Snackbar.LENGTH_SHORT).show()
                }

            }
            dialog.show()
        }

        navView= binding.navView
        navController = findNavController(R.id.nav_host_fragment_activity_main)

        setBottomBarVisibility()
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_favorite,
                R.id.navigation_alerts,
                R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        getLocation()

    }

    override fun onResume() {
        super.onResume()
        if(checkPermissions()){
            GPSLocation.getInstance(this).getLastLocation()
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_ID){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLocation()
            }
        }
    }
    private fun checkPermissions()=
        ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this,
            android.Manifest.permission.ACCESS_FINE_LOCATION) ==  PackageManager.PERMISSION_GRANTED
    private fun requestPermissions(){
        ActivityCompat.requestPermissions(this@MainActivity, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION
                ,android.Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_ID
        )
    }
    private fun isLocationEnabled():Boolean{
        val locationManager : LocationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
    private fun getLocation(){
        if (checkPermissions()){
            if (isLocationEnabled()){
                GPSLocation.getInstance(this).getLastLocation()
            }else{
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        }else{
            requestPermissions()
        }
    }

    private fun setBottomBarVisibility() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_home, R.id.navigation_favorite, R.id.navigation_alerts, R.id.navigation_settings -> {
                    binding.navView.visibility = View.VISIBLE
                }
                else -> {
                    binding.navView.visibility = View.GONE
                }
            }
        }
    }


}