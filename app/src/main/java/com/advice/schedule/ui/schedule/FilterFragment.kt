package com.advice.schedule.ui.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.advice.schedule.PreferenceViewModel
import com.advice.schedule.Response
import com.advice.schedule.dObj
import com.advice.schedule.models.firebase.FirebaseTag
import com.advice.schedule.models.firebase.FirebaseTagType
import com.advice.schedule.models.local.Location
import com.advice.schedule.ui.HackerTrackerViewModel
import com.advice.schedule.ui.activities.MainActivity
import com.advice.schedule.views.FilterAdapter
import com.shortstack.hackertracker.databinding.FilterFragmentBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class FilterFragment : Fragment() {

    private val preferenceViewModel by sharedViewModel<PreferenceViewModel>()
    private val viewModel by sharedViewModel<HackerTrackerViewModel>()

    private var _binding: FilterFragmentBinding? = null
    private val binding get() = _binding!!

    private val adapter: FilterAdapter = FilterAdapter({
        viewModel.toggleFilter(it)
    }, {
        (requireActivity() as MainActivity).showSchedule(it)
    })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FilterFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.list.adapter = adapter
        binding.action.setOnClickListener {
            viewModel.clearFilters()
        }

        binding.hint.isVisible = true
        binding.hint.setOnCloseListener {
            preferenceViewModel.markFiltersTutorialAsComplete()
        }

        viewModel.tags.observe(viewLifecycleOwner) {
            when (it) {
                is Response.Success -> {
                    val tags = it.data
                    setTypes(tags)
                    val hasFilters = it.data.flatMap { it.tags }.any { it.isSelected }
                    binding.action.isVisible = hasFilters
                }
            }
        }

        viewModel.events.observe(viewLifecycleOwner) {
            val list = it.data ?: emptyList()
            // only getting the used locations
            val locations = list.map { it.location }.distinctBy { it.id }
            setLocations(locations)
        }

        preferenceViewModel.getFilterTutorial().observe(viewLifecycleOwner) { shouldShowTutorial ->
            binding.hint.isVisible = shouldShowTutorial
        }
    }

    private fun setTypes(types: List<FirebaseTagType>) {
        merge(types, viewModel.events.value?.data?.map { it.location }?.distinctBy { it.id })
    }


    private fun setLocations(locations: List<Location>) {
        merge(viewModel.tags.value?.dObj as? List<FirebaseTagType>, locations)
    }

    private fun merge(types: List<FirebaseTagType>?, locations: List<Location>?) {
        val collection = ArrayList<Any>()

        collection.add(FirebaseTag.bookmark)

        types?.filter { it.category == "content" && it.is_browsable }?.forEach {
            collection.add(it.label)
            collection.addAll(it.tags)
        }

        if (locations != null) {
            collection.add("Locations")
            collection.addAll(locations)
        }

        adapter.setElements(collection)
    }
}