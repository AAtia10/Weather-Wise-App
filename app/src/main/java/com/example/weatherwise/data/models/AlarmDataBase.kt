package com.example.weatherwise.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Entity(tableName = "alarms")
@TypeConverters(AlarmConverter::class)
data class AlarmEntity(
    @PrimaryKey
    var id: String,
    val label: String,
    val time: Long,
    val duration: Long,
)
class AlarmConverter {
    @TypeConverter
    fun fromLocalDateTime(time: AlarmEntity): String {
        return Gson().toJson(time)
    }

    @TypeConverter
    fun toLocalDateTime(timeString: String?): AlarmEntity {
        return Gson().fromJson(timeString,AlarmEntity::class.java)
    }
}
