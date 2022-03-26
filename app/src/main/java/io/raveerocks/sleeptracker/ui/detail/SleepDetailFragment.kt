package io.raveerocks.sleeptracker.ui.detail

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
import io.raveerocks.sleeptracker.databinding.FragmentSleepDetailBinding
import io.raveerocks.sleeptracker.view.detail.SleepDetailViewModel
import io.raveerocks.sleeptracker.view.detail.SleepDetailViewModelFactory


class SleepDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val application = requireNotNull(this.activity).application
        val arguments = SleepDetailFragmentArgs.fromBundle(requireArguments())
        val dataSource = SleepDatabase.getInstance(application).sleepDatabaseDao
        val viewModelFactory = SleepDetailViewModelFactory(arguments.sleepNightKey, dataSource)
        val sleepDetailViewModel =
            ViewModelProvider(
                this, viewModelFactory
            )[SleepDetailViewModel::class.java]

        val binding: FragmentSleepDetailBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_sleep_detail, container, false
        )
        binding.lifecycleOwner = this
        binding.sleepDetailViewModel = sleepDetailViewModel

        sleepDetailViewModel.navigateToSleepTracker.observe(viewLifecycleOwner) {
            if (it == true) {
                this.findNavController().navigate(
                    SleepDetailFragmentDirections.actionSleepDetailFragmentToSleepTrackerFragment()
                )
                sleepDetailViewModel.doneNavigating()
            }
        }

        return binding.root
    }
}