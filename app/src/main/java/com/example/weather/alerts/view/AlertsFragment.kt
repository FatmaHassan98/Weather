package com.example.weather.alerts.view

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.airbnb.lottie.LottieAnimationView
import com.example.weather.R
import com.example.weather.alerts.AlertWorker
import com.example.weather.alerts.viewmodel.AlertViewModel
import com.example.weather.alerts.viewmodel.AlertViewModelFactory
import com.example.weather.database.room.AlertStatus
import com.example.weather.database.room.ConceretLocalSource
import com.example.weather.database.room.entity.EntityAlert
import com.example.weather.database.shared.prefernces.SharedPreferenceSource
import com.example.weather.database.shared.prefernces.Utaliltes
import com.example.weather.databinding.FragmentAlertsBinding
import com.example.weather.map.view.MapsActivity
import com.example.weather.model.repository.Repository
import com.example.weather.network.APIClient
import com.example.weather.network.APIState
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class AlertsFragment : Fragment() , AlertOnClicklistener{

    private lateinit var binding: FragmentAlertsBinding
    private lateinit var alertViewModel: AlertViewModel
    private lateinit var alertViewModelFactory: AlertViewModelFactory
    private lateinit var entityAlert: EntityAlert
    private lateinit var alertAdapter: AlertAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = FragmentAlertsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        alertViewModelFactory = AlertViewModelFactory(Repository.getInstance(APIClient.getInstance(),
            ConceretLocalSource(requireContext())))

        alertViewModel = ViewModelProvider(this,alertViewModelFactory)[AlertViewModel::class.java]

        binding.addAlert.setOnClickListener {
            if(checkForInternet(requireContext())){
                showDialog()
            }else{
                Snackbar.make(binding.root,"Please, check your network", Snackbar.LENGTH_SHORT).show()
            }
        }


        lifecycleScope.launch {
            alertViewModel.alert.collectLatest {
                when(it){
                    is AlertStatus.Loading ->{
                        binding.recyclerAlerts.visibility = View.GONE
                        binding.noAlert.visibility = View.VISIBLE
                        binding.animationNotification.visibility = View.VISIBLE
                    }
                    is AlertStatus.Success ->{
                        if(it.entityAlert.isNullOrEmpty()) {
                            binding.recyclerAlerts.visibility = View.GONE
                            binding.noAlert.visibility = View.VISIBLE
                            binding.animationNotification.visibility = View.VISIBLE
                        }else{
                            binding.recyclerAlerts.visibility = View.VISIBLE
                            binding.noAlert.visibility = View.GONE
                            binding.animationNotification.visibility = View.GONE

                            alertAdapter = AlertAdapter(requireContext(),this@AlertsFragment)

                            binding.recyclerAlerts.apply {
                                adapter = alertAdapter
                                alertAdapter.submitList(it.entityAlert)
                                layoutManager = LinearLayoutManager(context).apply {
                                    orientation = RecyclerView.VERTICAL
                                }
                            }
                        }
                    }
                    else ->{

                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog)
        val startDate = dialog.findViewById(R.id.startDate) as TextView
        val endDate = dialog.findViewById(R.id.endDate) as TextView
        val city = dialog.findViewById(R.id.cityAlertDialog) as TextView
        val location = dialog.findViewById(R.id.locationAlert) as LottieAnimationView
        val cancel = dialog.findViewById(R.id.cancelAlert) as Button
        val save = dialog.findViewById(R.id.saveAlert) as Button

        entityAlert = EntityAlert()

        cancel.setOnClickListener {
            dialog.dismiss()
        }

        startDate.setOnClickListener {

            val c :Calendar  = Calendar.getInstance();
            val mYear = c.get(Calendar.YEAR);
            val mMonth = c.get(Calendar.MONTH);
            val mDay = c.get(Calendar.DAY_OF_MONTH);

            val datePickerDialog = DatePickerDialog(requireContext(),
                { _, year, monthOfYear, dayOfMonth ->
                    startDate.text = formatDate(year,monthOfYear,dayOfMonth)

                    val cTime = Calendar.getInstance()
                    val mHour = cTime[Calendar.HOUR_OF_DAY]
                    val mMinute = cTime[Calendar.MINUTE]

                    val timePickerDialog = TimePickerDialog(requireContext(),
                        { _, hourOfDay, minute ->
                            startDate.append("\n     ${formatTime(hourOfDay, minute)}")
                            entityAlert.start = startDate.text.toString()
                        },
                        mHour,
                        mMinute,

                        false
                    )
                    timePickerDialog.show()

                }, mYear, mMonth, mDay
            )
            datePickerDialog.show()
        }

        endDate.setOnClickListener {

            val c :Calendar  = Calendar.getInstance();
            val mYear = c.get(Calendar.YEAR);
            val mMonth = c.get(Calendar.MONTH);
            val mDay = c.get(Calendar.DAY_OF_MONTH);

            val datePickerDialog = DatePickerDialog(requireContext(),
                 { _, year, monthOfYear, dayOfMonth ->
                    endDate.text = formatDate(year,monthOfYear,dayOfMonth)

                    val cTime = Calendar.getInstance()
                    val mHour = cTime[Calendar.HOUR_OF_DAY]
                    val mMinute = cTime[Calendar.MINUTE]

                    val timePickerDialog = TimePickerDialog(requireContext(),
                        { _, hourOfDay, minute ->
                            endDate.append("\n     ${formatTime(hourOfDay, minute)}")
                            entityAlert.end = endDate.text.toString()
                        },
                        mHour,
                        mMinute,
                        false
                    )
                    timePickerDialog.show()

                }, mYear, mMonth, mDay
            )
            datePickerDialog.show()
        }

        location.setOnClickListener {
            if (checkForInternet(requireContext())) {
                SharedPreferenceSource.getInstance(requireContext()).setMap("alert")
                val intent = Intent(requireActivity(), MapsActivity::class.java)
                requireActivity().startActivity(intent)

                alertViewModel.getWeather(SharedPreferenceSource.getInstance(requireContext()).getLatAlert(),
                    SharedPreferenceSource.getInstance(requireContext()).getLonAlert(),
                    SharedPreferenceSource.getInstance(requireContext()).getSavedUnit(),
                    SharedPreferenceSource.getInstance(requireContext()).getSavedLanguage()
                )

                lifecycleScope.launch {
                    alertViewModel.weather.collectLatest {
                        when (it) {
                            is APIState.Loading -> {

                            }
                            is APIState.Success -> {
                                city.text = getLocationFromLatAndLon(it.weather.lat, it.weather.lon)

                                entityAlert.lat = it.weather.lat
                                entityAlert.lon = it.weather.lon
                                entityAlert.current = it.weather.current
                                if (!it.weather.alerts.isNullOrEmpty()) {
                                    entityAlert.alert = it.weather.alerts
                                }
                            }
                            else -> {

                            }
                        }
                    }
                }
            }else{
                Snackbar.make(binding.root,"Please, Check your connection",Snackbar.LENGTH_SHORT)
                    .show()
            }
        }

        save.setOnClickListener {
            if (startDate.text.isNotEmpty() &&
                endDate.text.isNotEmpty() &&
                city.text.isNotEmpty() &&
                (dialog.findViewById<RadioButton>(
                    R.id.notificationAlert
                ).isChecked || dialog.findViewById<RadioButton>(R.id.alarmAlert).isChecked)
            ) {
                if (dialog.findViewById<RadioButton>(R.id.notificationAlert).isChecked) {
                    entityAlert.notification = "notification"
                } else if (dialog.findViewById<RadioButton>(R.id.alarmAlert).isChecked) {
                    entityAlert.notification = "alarm"
                }

                alertViewModel.insertAlert(entityAlert)

                val inputData = Data.Builder()
                inputData.putString(Utaliltes.ID, entityAlert.id)
                inputData.putLong(Utaliltes.TIME,10000)

                val myWorkRequest: WorkRequest =
                    OneTimeWorkRequestBuilder<AlertWorker>()
                        .setInputData(inputData.build())
                        .build()

                WorkManager.getInstance(requireContext())
                    .enqueue(myWorkRequest)

                dialog.dismiss()
            }else{
                Snackbar.make(binding.root,"Please, entre all data",Snackbar.LENGTH_SHORT).show()
            }
        }

        dialog.show()

    }

    private fun checkForInternet(context: Context): Boolean {

        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            val network = connectivityManager.activeNetwork ?: return false

            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

                else -> false
            }
        } else {
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }

    private fun formatDate(year: Int, month: Int, day: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)
        val dateFormat = SimpleDateFormat("MMM dd, yyyy - EEE", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun formatTime(hourOfDay: Int, minute: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun getLocationFromLatAndLon(lat: Double, lon: Double): String {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val address = geocoder.getFromLocation(lat, lon, 1) as List<Address>
        return if (address.isNotEmpty()) {
            if (address[0].locality == null){
                "null"
            }else{
                address[0].locality
            }
        }else
            "null"    }

    override fun deleteAlert(entityAlert: EntityAlert) {
        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(context)
        builder.setMessage("Are you sure!\nYou want to dismiss this item ?")
        builder.setCancelable(false)
        builder.setPositiveButton("Yes"){ dialog: DialogInterface, _: Int ->
            alertViewModel.deleteAlert(entityAlert)
            dialog.cancel()
        }
        builder.setNegativeButton("No"){ dialog: DialogInterface, _: Int ->
            dialog.cancel()
        }
        val alertDialog: android.app.AlertDialog? = builder.create()
        alertDialog?.show()
    }
}