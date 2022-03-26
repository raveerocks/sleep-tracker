package io.raveerocks.sleeptracker.view.tracker

import android.app.Application
import androidx.lifecycle.*
import io.raveerocks.sleeptracker.database.SleepDatabaseDao
import io.raveerocks.sleeptracker.database.SleepNight
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SleepTrackerViewModel(val database: SleepDatabaseDao, application: Application) :
    AndroidViewModel(application) {

    private val runningNight = MutableLiveData<SleepNight?>()
    private val _navigateToSleepQualityEvent = MutableLiveData<SleepNight?>()
    private var _showSnackBarEvent = MutableLiveData<Boolean>()
    private val _navigateToSleepDataQualityEvent = MutableLiveData<Long?>()

    val nights = database.getAllNights()
    val startButtonVisible = Transformations.map(runningNight) {
        null == it
    }
    val stopButtonVisible = Transformations.map(runningNight) {
        null != it
    }
    val clearButtonVisible = Transformations.map(nights) {
        it?.isNotEmpty()
    }
    val navigateToSleepQualityEvent: LiveData<SleepNight?>
        get() = _navigateToSleepQualityEvent
    val showSnackBarEvent: LiveData<Boolean>
        get() = _showSnackBarEvent
    val navigateToSleepDataQualityEvent
        get() = _navigateToSleepDataQualityEvent


    init {
        initializeRunningNight()
    }

    fun onStartTracking() {
        viewModelScope.launch {
            val newNight = SleepNight()
            insert(newNight)
            runningNight.value = getLastRunningNight()
        }
    }

    fun onStopTracking() {
        viewModelScope.launch {
            val oldNight = runningNight.value ?: return@launch
            oldNight.endTimeMilli = System.currentTimeMillis()
            update(oldNight)
            _navigateToSleepQualityEvent.value = oldNight
        }
    }

    fun onClear() {
        viewModelScope.launch {
            delete()
            runningNight.value = null
            _showSnackBarEvent.value = true
        }
    }

    fun onSleepNightClicked(id: Long) {
        _navigateToSleepDataQualityEvent.value = id
    }

    fun onSleepDataQualityNavigated() {
        _navigateToSleepDataQualityEvent.value = null
    }

    fun doneShowingSnackBar() {
        _showSnackBarEvent.value = false
    }

    fun doneNavigating() {
        _navigateToSleepQualityEvent.value = null
    }

    private fun initializeRunningNight() {
        viewModelScope.launch {
            runningNight.value = getLastRunningNight()
        }
    }

    private suspend fun getLastRunningNight(): SleepNight? {
        return withContext(Dispatchers.IO) {
            var lastNight = database.getLastNight()
            if (lastNight?.endTimeMilli != lastNight?.startTimeMilli) {
                lastNight = null
            }
            lastNight
        }
    }

    private suspend fun insert(night: SleepNight) {
        withContext(Dispatchers.IO) {
            database.insert(night)
        }
    }

    private suspend fun update(night: SleepNight) {
        withContext(Dispatchers.IO) {
            database.update(night)
        }
    }

    private suspend fun delete() {
        withContext(Dispatchers.IO) {
            database.deleteAllNights()
        }
    }

}

