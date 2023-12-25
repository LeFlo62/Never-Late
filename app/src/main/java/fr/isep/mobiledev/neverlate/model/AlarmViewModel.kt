package fr.isep.mobiledev.neverlate.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import fr.isep.mobiledev.neverlate.entities.Alarm
import fr.isep.mobiledev.neverlate.repository.AlarmRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmViewModel(private val repository : AlarmRepository) : ViewModel() {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    val allAlarms : LiveData<List<Alarm>> = repository.allAlarms.asLiveData()

    fun insert(alarm: Alarm) = coroutineScope.launch {
        repository.insert(alarm)
    }

    fun delete(alarm: Alarm) = coroutineScope.launch {
        repository.delete(alarm)
    }

    fun getAlarmById(id: Int) : LiveData<Alarm> {
        return repository.getAlarmById(id).asLiveData()
    }

    fun update(alarm: Alarm) = coroutineScope.launch {
        repository.update(alarm)
    }

    fun upsert(alarm: Alarm) = coroutineScope.launch {
        repository.upsert(alarm)
    }

    fun deleteAlarms(alarms : List<Alarm>) = coroutineScope.launch {
        repository.deleteAlarms(alarms)
    }

}

class AlarmViewModelFactory(private val repository: AlarmRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlarmViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AlarmViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}