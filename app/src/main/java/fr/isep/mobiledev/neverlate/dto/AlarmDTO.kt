package fr.isep.mobiledev.neverlate.dto

import android.os.Parcel
import android.os.Parcelable
import fr.isep.mobiledev.neverlate.converter.RuleConverter
import fr.isep.mobiledev.neverlate.entities.Alarm
import fr.isep.mobiledev.neverlate.rules.Rule

class AlarmDTO(
    var id: Int = 0,
    var name: String? = "",
    var hour: Int = 0,
    var minute: Int = 0,
    var toggled: Boolean = false,
    var rules: List<Rule> = listOf()
) : Parcelable {


    constructor(alarm : Alarm) : this(alarm.id, alarm.name, alarm.hour, alarm.minute, alarm.toggled, alarm.rules)

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readByte() != 0.toByte(),
        RuleConverter.gson.fromJson(parcel.readString(), Array<Rule>::class.java).toList()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeInt(hour)
        parcel.writeInt(minute)
        parcel.writeByte(if (toggled) 1 else 0)
        parcel.writeString(RuleConverter.gson.toJson(rules))
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AlarmDTO> {
        const val ALARM_EXTRA = "fr.isep.mobiledev.neverlate.ALARM_EXTRA"

        override fun createFromParcel(parcel: Parcel): AlarmDTO {
            return AlarmDTO(parcel)
        }

        override fun newArray(size: Int): Array<AlarmDTO?> {
            return arrayOfNulls(size)
        }
    }


}