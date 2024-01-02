package fr.isep.mobiledev.neverlate.entities

import android.text.format.DateFormat
import androidx.room.Entity
import androidx.room.PrimaryKey
import fr.isep.mobiledev.neverlate.rules.Puzzle
import fr.isep.mobiledev.neverlate.rules.PuzzleNone
import fr.isep.mobiledev.neverlate.rules.Rule
import java.util.Calendar

@Entity
data class Alarm (
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var name: String = "",
    var hour: Int = 0,
    var minute: Int = 0,
    var toggled: Boolean = false,
    var rules: List<Rule> = listOf(),
    var puzzle: Puzzle = PuzzleNone(),
    var smsPhoneNumber: String = "",
    var smsMessage: String = ""
) {
    fun getNextExecution() : Long {
        var time = System.currentTimeMillis()
        println("Base")
        println(DateFormat.format("dd-MM-yyyy HH:mm:ss", time))
        for(rule in rules.sortedBy { it.getOrder() }) {
            time = rule.getNextExecution(time)
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = time
            println(rule.getClassName())
            println(DateFormat.format("dd-MM-yyyy HH:mm:ss", calendar))
        }
        //Add hour and minute
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = time
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)

        println("Final")
        println(DateFormat.format("dd-MM-yyyy HH:mm:ss", calendar))

        return calendar.timeInMillis
    }
}