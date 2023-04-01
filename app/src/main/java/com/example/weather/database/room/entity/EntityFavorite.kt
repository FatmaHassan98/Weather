package com.example.weather.database.room.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.weather.model.pojos.Current
import com.example.weather.model.pojos.Daily
import com.example.weather.model.pojos.Hourly

@Entity(tableName = "Favorite")
data class EntityFavorite (@PrimaryKey(autoGenerate = true) var id: Int ,
                          var lat:Double, var lon:Double, var current: Current?,
                          var hourly:List<Hourly>, var daily:List<Daily>) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readDouble(),
        parcel.readDouble(),
        TODO("current"),
        TODO("hourly"),
        TODO("daily")
    ) {
    }

    constructor():this(0,0.0, 0.0, null, emptyList(),  emptyList())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeDouble(lat)
        parcel.writeDouble(lon)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<EntityFavorite> {
        override fun createFromParcel(parcel: Parcel): EntityFavorite {
            return EntityFavorite(parcel)
        }

        override fun newArray(size: Int): Array<EntityFavorite?> {
            return arrayOfNulls(size)
        }
    }

}