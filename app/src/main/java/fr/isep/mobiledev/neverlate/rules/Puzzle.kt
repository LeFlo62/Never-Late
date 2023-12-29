package fr.isep.mobiledev.neverlate.rules

import androidx.compose.runtime.Composable
import fr.isep.mobiledev.neverlate.converter.Convertable
import fr.isep.mobiledev.neverlate.dto.AlarmDTO

interface Puzzle : Convertable {

    @Composable
    fun Content(alarm : AlarmDTO, onSnooze : () -> Unit, onDismiss : () -> Unit)

}