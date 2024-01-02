package fr.isep.mobiledev.neverlate.rules

import fr.isep.mobiledev.neverlate.converter.Convertable

interface Rule : Convertable {

    fun getNextExecution(time : Long) : Long

    fun getOrder() : Int

}