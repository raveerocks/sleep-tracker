package io.raveerocks.sleeptracker.view.quality

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.raveerocks.sleeptracker.database.SleepDatabaseDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SleepQualityViewModel(
    private val sleepNightKey: Long = 0L,
    val database: SleepDatabaseDao
) : ViewModel() {

    private val _navigateToSleepTracker = MutableLiveData<Boolean?>()

    val navigateToSleepTracker: LiveData<Boolean?>
        get() = _navigateToSleepTracker

    fun onSetSleepQuality(quality: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val tonight = database.get(sleepNightKey) ?: return@withContext
                tonight.sleepQuality = quality
                database.update(tonight)
            }
            _navigateToSleepTracker.value = true
        }
    }

    fun doneNavigating() {
        _navigateToSleepTracker.value = null
    }

}