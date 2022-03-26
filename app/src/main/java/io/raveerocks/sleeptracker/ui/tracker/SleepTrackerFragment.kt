package io.raveerocks.sleeptracker.ui.tracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import io.raveerocks.sleeptracker.R
import io.raveerocks.sleeptracker.adapter.tracker.SleepNightAdapter
import io.raveerocks.sleeptracker.adapter.tracker.SleepNightListener
import io.raveerocks.sleeptracker.database.SleepDatabase
import io.raveerocks.sleeptracker.databinding.FragmentSleepTrackerBinding
import io.raveerocks.sleeptracker.view.tracker.SleepTrackerViewModel
import io.raveerocks.sleeptracker.view.tracker.SleepTrackerViewModelFactory


class SleepTrackerFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val application = requireNotNull(this.activity).application
        val dataSource = SleepDatabase.getInstance(application).sleepDatabaseDao
        val viewModelFactory = SleepTrackerViewModelFactory(dataSource, application)
        val sleepTrackerViewModel =
            ViewModelProvider(this, viewModelFactory)[SleepTrackerViewModel::class.java]
        val gridLayoutManager = GridLayoutManager(activity, 3)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int) = when (position) {
                0 -> 3
                else -> 1
            }
        }
        val adapter = SleepNightAdapter(SleepNightListener { nightId ->
            sleepTrackerViewModel.onSleepNightClicked(nightId)
        })

        val binding: FragmentSleepTrackerBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_sleep_tracker, container, false
        )
        binding.lifecycleOwner = this
        binding.sleepTrackerViewModel = sleepTrackerViewModel
        binding.sleepList.layoutManager = gridLayoutManager
        binding.sleepList.adapter = adapter

        sleepTrackerViewModel.nights.observe(viewLifecycleOwner) {
            it?.let {
                adapter.addHeaderAndSubmitList(it)
            }
        }

        sleepTrackerViewModel.showSnackBarEvent.observe(viewLifecycleOwner) {
            if (it == true) {
                Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    getString(R.string.cleared_message),
                    Snackbar.LENGTH_SHORT
                ).show()
                sleepTrackerViewModel.doneShowingSnackBar()
            }
        }

        sleepTrackerViewModel.navigateToSleepQualityEvent.observe(viewLifecycleOwner) { night ->
            night?.let {
                this.findNavController().navigate(
                    SleepTrackerFragmentDirections
                        .actionSleepTrackerFragmentToSleepQualityFragment(night.nightId)
                )
                sleepTrackerViewModel.doneNavigating()
            }
        }

        sleepTrackerViewModel.navigateToSleepDataQualityEvent.observe(viewLifecycleOwner) { night ->
            night?.let {
                this.findNavController().navigate(
                    SleepTrackerFragmentDirections
                        .actionSleepTrackerFragmentToSleepDetailFragment(night)
                )
                sleepTrackerViewModel.onSleepDataQualityNavigated()
            }
        }

        return binding.root
    }
}
