package fr.isep.mobiledev.neverlate.dto

import android.os.Parcel
import android.os.Parcelable
import fr.isep.mobiledev.neverlate.converter.PuzzleConverter
import fr.isep.mobiledev.neverlate.converter.RuleConverter
import fr.isep.mobiledev.neverlate.entities.Alarm
import fr.isep.mobiledev.neverlate.rules.Puzzle
import fr.isep.mobiledev.neverlate.rules.PuzzleNone
import fr.isep.mobiledev.neverlate.rules.Rule

class AlarmDTO(
    var id: Int = 0,
    var name: String = "",
    var hour: Int = 0,
    var minute: Int = 0,
    var toggled: Boolean = false,
    var rules: List<Rule> = listOf(),
    var puzzle : Puzzle = PuzzleNone(),
    var smsPhoneNumber: String = "",
    var smsMessage: String = ""
) : Parcelable {

    constructor(alarm : Alarm) : this(alarm.id, alarm.name, alarm.hour, alarm.minute, alarm.toggled, alarm.rules, alarm.puzzle, alarm.smsPhoneNumber, alarm.smsMessage)

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readInt(),
        parcel.readByte() != 0.toByte(),
        RuleConverter.gson.fromJson(parcel.readString(), Array<Rule>::class.java).toList(),
        PuzzleConverter.gson.fromJson(parcel.readString(), Puzzle::class.java),
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeInt(hour)
        parcel.writeInt(minute)
        parcel.writeByte(if (toggled) 1 else 0)
        parcel.writeString(RuleConverter.gson.toJson(rules))
        parcel.writeString(PuzzleConverter.gson.toJson(puzzle))
        parcel.writeString(smsPhoneNumber)
        parcel.writeString(smsMessage)
    }

    override fun describeContents(): Int {
        return 0
    }

    fun toAlarm() : Alarm {
        return Alarm(id, name, hour, minute, toggled, rules, puzzle, smsPhoneNumber, smsMessage)
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