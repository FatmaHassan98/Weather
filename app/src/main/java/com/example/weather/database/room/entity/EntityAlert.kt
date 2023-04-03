package com.example.weather.database.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.weather.model.pojos.Alert
import com.example.weather.model.pojos.Current
import java.util.*

@Entity(tableName = "Alert")
data class EntityAlert(@PrimaryKey var id: String = UUID.randomUUID().toString(),
                       var lat:Double, var lon:Double, var current: Current?,
                       var alert:List<Alert>, var notification:String,
                       var start: String?, var end: String?
) {

    constructor():this(UUID.randomUUID().toString(),0.0,0.0,null, emptyList(),
        "", null, null)


}