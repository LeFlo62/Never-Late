package fr.isep.mobiledev.neverlate.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import fr.isep.mobiledev.neverlate.converter.RuleConverter
import fr.isep.mobiledev.neverlate.rules.Rule

@Entity
data class Alarm (
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var name: String = "",
    var hour: Int = 0,
    var minute: Int = 0,
    var toggled: Boolean = false,
    var rules: List<Rule> = listOf()
) {
    fun getNextExecution() : Long {
        var time = System.currentTimeMillis()
        for(rule in rules) {
            time = rule.getNextExecution(time)
        }
        //Add hour and minute
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = time
        calendar.set(java.util.Calendar.HOUR_OF_DAY, hour)
        calendar.set(java.util.Calendar.MINUTE, minute)
        return calendar.timeInMillis
    }
}