package io.raveerocks.sleeptracker.view.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.raveerocks.sleeptracker.database.SleepDatabaseDao
import io.raveerocks.sleeptracker.database.SleepNight

class SleepDetailViewModel(sleepNightKey: Long = 0L, dataSource: SleepDatabaseDao) :
    ViewModel() {

    private val database = dataSource
    private val night = MediatorLiveData<SleepNight>()
    private val _navigateToSleepTracker = MutableLiveData<Boolean?>()

    val navigateToSleepTracker: LiveData<Boolean?>
        get() = _navigateToSleepTracker

    init {
        night.addSource(database.getNightWithId(sleepNightKey), night::setValue)
    }

    fun getNight() = night

    fun doneNavigating() {
        _navigateToSleepTracker.value = null
    }

    fun onClose() {
        _navigateToSleepTracker.value = true
    }

}

 