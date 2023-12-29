package fr.isep.mobiledev.neverlate.converter

import androidx.room.TypeConverter
import com.google.gson.GsonBuilder
import fr.isep.mobiledev.neverlate.rules.Puzzle
import java.io.Serializable

class PuzzleConverter : Serializable {

    companion object {
        val gson = GsonBuilder()
            .registerTypeAdapter(Puzzle::class.java, ConvertableDeserializer<Puzzle>())
            .create()
    }

    @TypeConverter
    fun fromPuzzle(puzzle : Puzzle): String {
        return gson.toJson(puzzle)
    }

    @TypeConverter
    fun toPuzzle(rules: String): Puzzle {
        return gson.fromJson(rules, Puzzle::class.java)
    }

}