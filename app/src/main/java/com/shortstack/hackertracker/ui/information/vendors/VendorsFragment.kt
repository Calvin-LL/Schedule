package com.shortstack.hackertracker.ui.information.vendors

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.shortstack.hackertracker.models.firebase.FirebaseVendor
import com.shortstack.hackertracker.ui.HackerTrackerViewModel
import com.shortstack.hackertracker.ui.ListFragment
import com.shortstack.hackertracker.ui.activities.MainActivity


class VendorsFragment : ListFragment<FirebaseVendor>() {

    companion object {
        fun newInstance() = VendorsFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel = ViewModelProvider(requireActivity())[HackerTrackerViewModel::class.java]

        viewModel.vendors.observe(viewLifecycleOwner, Observer {
            onResource(it)
        })
    }
}



