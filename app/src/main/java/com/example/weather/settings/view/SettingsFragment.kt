package com.example.weather.settings.view

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.weather.database.shared.prefernces.SharedPreferenceSource
import com.example.weather.databinding.FragmentSettingsBinding
import com.example.weather.settings.viewmodel.SettingsViewModel
import com.example.weather.settings.viewmodel.SettingsViewModelFactory

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var settingsViewModelFactory: SettingsViewModelFactory

    private lateinit var location:String
    private lateinit var language:String
    private lateinit var temperature:String
    private lateinit var wind:String
    private lateinit var notification: String
    private lateinit var theme:String
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

        binding.btnSave.setOnClickListener{
            location = if(binding.gpsLocation.isChecked) {
                "GPS"
            } else {
                "Map"
            }

            language = if (binding.arabicLanguage.isChecked){
                "arabic"
            }else{
                "english"
            }

            temperature = if(binding.celsiusTemperature.isChecked){
                "metric"
            }else if(binding.kelvinTemperature.isChecked){
                "default"
            }else{
                "imperial"
            }

            wind = if (binding.meterWind.isChecked){
                "metric"
            }else{
                "imperial"
            }

            notification = if (binding.enableNotification.isChecked){
                "enable"
            }else{
                "disable"
            }

            theme = if (binding.lightTheme.isChecked){
                "light"
            }else{
                "dark"
            }

            settingsViewModel.setSetting(location, language, temperature,
                wind, notification, theme)

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