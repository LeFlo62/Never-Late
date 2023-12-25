package fr.isep.mobiledev.neverlate.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Alarm (
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var name: String = "",
    var hour: Int = 0,
    var minute: Int = 0,
    var toggled: Boolean = false
)