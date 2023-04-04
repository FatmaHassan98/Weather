package com.example.weather.alerts

import android.app.*
import android.content.Context
import android.content.Context.WINDOW_SERVICE
import android.content.Intent
import android.graphics.PixelFormat
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.weather.R
import com.example.weather.database.room.ConceretLocalSource
import com.example.weather.database.room.WeatherDao
import com.example.weather.database.room.WeatherDatabase
import com.example.weather.database.room.entity.EntityAlert
import com.example.weather.database.shared.prefernces.SharedPreferenceSource
import com.example.weather.database.shared.prefernces.Utaliltes
import com.example.weather.home.model.GPSLocation
import com.example.weather.model.repository.Repository
import com.example.weather.network.APIClient
import com.example.weather.view.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.*

class AlertWorker (private var context: Context, private var workerParameters: WorkerParameters )
    : CoroutineWorker(context,workerParameters){

    private lateinit var notificationManager: NotificationManager
    private lateinit var repository:Repository

    private var LAYOUT_FLAG  = 0

    var description = ""
    var content =""

    override suspend fun doWork(): Result {
        val weatherDao : WeatherDao by lazy {
            val appDataBase: WeatherDatabase = WeatherDatabase.getInstance(context)
            appDataBase.getHomeWeather()
        }

        repository = Repository.getInstance(APIClient.getInstance(),ConceretLocalSource(weatherDao))


        val start = inputData.getLong(Utaliltes.TIME,0)
        val id = inputData.getString(Utaliltes.ID)
        lateinit var resultData:Result

        LAYOUT_FLAG = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else
            WindowManager.LayoutParams.TYPE_PHONE

        delay(start)

        try{
            val entityAlert = repository.getAlertById(id.toString())

            if(checkForInternet(context)){
               val response = repository.getWeatherAlert(
                   entityAlert.lat,
                   entityAlert.lon,
                    SharedPreferenceSource.getInstance(context).getSavedUnit(),
                    SharedPreferenceSource.getInstance(context).getSavedLanguage())

                withContext(Dispatchers.IO){
                    if (!response.equals(null)){
                        if (!response.alerts.isNullOrEmpty()) {
                            for (i in 0 until (response.alerts.size)) {
                                description +=   response.alerts[i].sender_name +"\n"
                            }
                        }else{
                            description = "It’s Fine"
                        }
                        resultData= Result.success()

                    }else{
                        description = "It’s Fine"
                        resultData= Result.failure()
                    }
                }

                if (SharedPreferenceSource.getInstance(context).getSavedNotificationStatus()
                    =="enable") {

                    if (entityAlert.notification == "notification") {
                        notification(description)
                        repository.deleteAlert(entityAlert)
                    } else {
                        alarm(context,description,entityAlert)
                    }
                }

            }else{
                if (SharedPreferenceSource.getInstance(context).getSavedNotificationStatus()=="enable") {

                    if (entityAlert.alert.isEmpty()){
                        content = "It’s Fine"
                    }else{
                        if (entityAlert.alert.isNotEmpty()) {
                            for (i in 0 until (entityAlert.alert.size)) {
                                content += entityAlert.alert[i].sender_name + ", "
                            }
                        }else{
                            content = "It’s Fine"
                        }
                    }

                    if (entityAlert.notification == "notification") {
                        notification(content)
                        repository.deleteAlert(entityAlert)
                    } else {
                        alarm(context,content,entityAlert)

                    }
                }
            }

        }catch (e:Exception){
            e.printStackTrace()
            resultData= Result.failure()
        }

        return resultData
    }

    private fun notification(description : String){

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "CHANNEL_ID",
                "CHANNEL_ID",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "Description"

            notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context!!, "CHANNEL_ID")
            .setSmallIcon(R.drawable.weather)
            .setContentTitle("Weather Alert")
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notificationManager.notify(0, builder.build())

    }

    private suspend fun alarm(context: Context, message: String,entityAlert: EntityAlert) {

        val mediaPlayer = MediaPlayer.create(context, R.raw.alarm_sound)

        val view: View = LayoutInflater.from(context).inflate(R.layout.item_alarm, null, false)
        val dismissBtn = view.findViewById(R.id.btnDismissAlarm) as Button
        val textView = view.findViewById(R.id.descriptionAlarm) as TextView
        val layoutParams =
            WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            )
        layoutParams.gravity = Gravity.TOP

        val windowManager = context.getSystemService(WINDOW_SERVICE) as WindowManager

        withContext(Dispatchers.Main) {
            windowManager.addView(view, layoutParams)
            view.visibility = View.VISIBLE
            textView.text = message
        }

        mediaPlayer.start()
        mediaPlayer.isLooping = true
        dismissBtn.setOnClickListener {
            mediaPlayer?.release()
            windowManager.removeView(view)
        }
        repository.deleteAlert(entityAlert)
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
}
