package com.example.weather.settings.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.weather.R
import com.example.weather.database.shared.prefernces.SharedPreferenceSource
import com.example.weather.databinding.FragmentSettingsBinding
import com.example.weather.map.view.MapsActivity
import com.example.weather.settings.viewmodel.SettingsViewModel
import com.example.weather.settings.viewmodel.SettingsViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var settingsViewModelFactory: SettingsViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        activity?.actionBar?.hide()

        binding = FragmentSettingsBinding.inflate(inflater, container, false)
      return binding.root

    }

    @SuppressLint("ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settingsViewModelFactory = SettingsViewModelFactory(SharedPreferenceSource.getInstance(requireContext()))

        settingsViewModel = ViewModelProvider(this,settingsViewModelFactory)[SettingsViewModel::class.java]

        binding.gpsLocation.setOnClickListener {
            settingsViewModel.setLocationWay("GPS")
        }

        binding.mapLocation.setOnClickListener {
            settingsViewModel.setLocationWay("Map")
            SharedPreferenceSource.getInstance(requireContext()).setMap("Home")
            val intent = Intent(requireActivity(),MapsActivity::class.java)
            requireActivity().startActivity(intent)
        }

        binding.arabicLanguage.setOnClickListener {
            settingsViewModel.setLanguage("ar")
        }

        binding.englishLanguage.setOnClickListener {
            settingsViewModel.setLanguage("en")
        }

        binding.celsiusTemperature.setOnClickListener {
            settingsViewModel.setUnit("metric")
        }

        binding.kelvinTemperature.setOnClickListener {
            settingsViewModel.setUnit("default")
        }

        binding.fahrenheitTemperature.setOnClickListener {
            settingsViewModel.setUnit("imperial")
        }

        binding.meterWind.setOnClickListener {
            settingsViewModel.setUnit("metric")
        }

        binding.mileWind.setOnClickListener {
            settingsViewModel.setUnit("imperial")
        }

        binding.enableNotification.setOnClickListener {
            settingsViewModel.setNotification("enable")
        }

        binding.disableNotification.setOnClickListener {
            settingsViewModel.setNotification("disable")
        }

        binding.lightTheme.setOnClickListener {
            settingsViewModel.setTheme("light")
        }

        binding.lightTheme.setOnClickListener {
            settingsViewModel.setTheme("dark")
        }

        if (settingsViewModel.getSavedLocationWay()=="GPS"){
            binding.gpsLocation.isChecked = true
        }else{
            binding.mapLocation.isChecked = true
        }

        if (settingsViewModel.getSavedLanguage() == "arabic"){
            binding.arabicLanguage.isChecked = true
        }else{
            binding.englishLanguage.isChecked = true
        }

        if (settingsViewModel.getSavedTemperatureUnit() == "metric"){
            binding.celsiusTemperature.isChecked = true
        }else if(settingsViewModel.getSavedTemperatureUnit() == "default"){
            binding.kelvinTemperature.isChecked = true
        }else{
            binding.fahrenheitTemperature.isChecked = true
        }

        if (settingsViewModel.getSavedWindSpeedUnit() == "metric"){
            binding.meterWind.isChecked = true
        }else{
            binding.mileWind.isChecked = true
        }

        if (settingsViewModel.getSavedNotificationStatus() == "enable"){
            binding.enableNotification.isChecked = true
        }else{
            binding.disableNotification.isChecked = true
        }

        if (settingsViewModel.getSavedTheme() == "light"){
            binding.lightTheme.isChecked = true
        }else{
            binding.darkTheme.isChecked = true
        }

    }


}