package fr.isep.mobiledev.neverlate.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import fr.isep.mobiledev.neverlate.rules.DayOfWeek
import fr.isep.mobiledev.neverlate.rules.MonthOfYear
import fr.isep.mobiledev.neverlate.rules.PreciseDate
import fr.isep.mobiledev.neverlate.rules.Rule
import fr.isep.mobiledev.neverlate.rules.WeekOfYear
import java.io.Serializable

class RuleConverter : Serializable {

    companion object {
        val gson = GsonBuilder()
            .registerTypeAdapter(Rule::class.java, ConvertableDeserializer<Rule>())
            .create()
    }

    @TypeConverter
    fun fromRules(rules: List<Rule>): String {
        val rulesArray = JsonArray()
        rules.forEach {
            rulesArray.add(gson.toJsonTree(it))
        }
        return rulesArray.toString()
    }

    @TypeConverter
    fun toRules(rules: String): List<Rule> {
        val rulesArray = gson.fromJson(rules, JsonArray::class.java)
        val rulesList = mutableListOf<Rule>()
        rulesArray.forEach {
            val rule = gson.fromJson(it, Rule::class.java)
            rulesList.add(rule)
        }
        return rulesList
    }

}