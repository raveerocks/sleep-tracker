package io.raveerocks.sleeptracker.ui.quality

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import io.raveerocks.sleeptracker.R
import io.raveerocks.sleeptracker.database.SleepDatabase
import io.raveerocks.sleeptracker.databinding.FragmentSleepQualityBinding
import io.raveerocks.sleeptracker.view.quality.SleepQualityViewModel
import io.raveerocks.sleeptracker.view.quality.SleepQualityViewModelFactory

class SleepQualityFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val application = requireNotNull(this.activity).application
        val arguments = SleepQualityFragmentArgs.fromBundle(requireArguments())
        val dataSource = SleepDatabase.getInstance(application).sleepDatabaseDao
        val viewModelFactory = SleepQualityViewModelFactory(arguments.sleepNightKey, dataSource)
        val sleepQualityViewModel =
            ViewModelProvider(
                this, viewModelFactory
            )[SleepQualityViewModel::class.java]

        val binding: FragmentSleepQualityBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_sleep_quality, container, false
        )
        binding.lifecycleOwner = this
        binding.sleepQualityViewModel = sleepQualityViewModel

        sleepQualityViewModel.navigateToSleepTracker.observe(viewLifecycleOwner) {
            if (it == true) {
                this.findNavController().navigate(
                    SleepQualityFragmentDirections.actionSleepQualityFragmentToSleepTrackerFragment()
                )
                sleepQualityViewModel.doneNavigating()
            }
        }

        return binding.root
    }
}
