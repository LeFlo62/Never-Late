package fr.isep.mobiledev.neverlate.rules

import java.util.Calendar

class DayOfWeek(val days: List<Boolean>) : Rule {
    private val className: String = this::class.java.name
    override fun getNextExecution(time: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time
        var day = calendar.get(Calendar.DAY_OF_WEEK)
        var i = 0
        while (!days[day - 1]) {
            day = (day % 7) + 1
            i++
        }
        calendar.add(Calendar.DAY_OF_MONTH, i)
        return calendar.timeInMillis
    }
    override fun getClassName(): String {
        return className
    }
}

class WeekOfYear(val period : Int, val offset : Int) : Rule {
    private val className: String = this::class.java.name
    override fun getNextExecution(time: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time
        var week = calendar.get(Calendar.WEEK_OF_YEAR)
        var i = 0
        while (week % period != offset) {
            week = (week % 52) + 1
            i++
        }
        calendar.add(Calendar.WEEK_OF_YEAR, i)
        return calendar.timeInMillis
    }
    override fun getClassName(): String {
        return className
    }
}

class MonthOfYear(val months : List<Boolean>) : Rule {
    private val className: String = this::class.java.name
    override fun getNextExecution(time: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time
        var month = calendar.get(Calendar.MONTH)
        var i = 0
        while (!months[month]) {
            month = (month % 12) + 1
            i++
        }
        calendar.add(Calendar.MONTH, i)
        return calendar.timeInMillis
    }
    override fun getClassName(): String {
        return className
    }
}

class PreciseDate(val day: Int, val month: Int, val year: Int) : Rule {
    private val className: String = this::class.java.name

    constructor(calendar : Calendar) : this(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR))

    override fun getNextExecution(time: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time
        calendar.set(Calendar.DAY_OF_MONTH, day)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.YEAR, year)
        if (calendar.timeInMillis < time) {
            calendar.add(Calendar.YEAR, 1)
        }
        return calendar.timeInMillis
    }
    override fun getClassName(): String {
        return className
    }
}