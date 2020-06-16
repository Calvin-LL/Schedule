package com.shortstack.hackertracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.shortstack.hackertracker.R
import kotlinx.android.synthetic.main.fragment_recyclerview.*

abstract class ListFragment<T> : Fragment() {

    private val adapter = ListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recyclerview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        list.adapter = adapter
    }

    inline fun <reified J : ViewModel> getViewModel(): J =
        ViewModelProviders.of(this).get(J::class.java)

    fun onResource(list: List<Any>?) {
        if (list == null) {
            setProgressIndicator(active = true)
            showInitView()
        } else {
            setProgressIndicator(active = false)
            adapter.clearAndNotify()

            if (list.isNotEmpty()) {
                adapter.addAllAndNotify(list)
                hideViews()
            } else {
                showEmptyView()
            }
        }
    }

    private fun setProgressIndicator(active: Boolean) {
        loading_progress.visibility = if (active) View.VISIBLE else View.GONE
    }

    private fun showInitView() {
        empty_view.visibility = View.VISIBLE
        empty_view.showDefault()
    }

    private fun showEmptyView() {
        empty_view.visibility = View.VISIBLE
        empty_view.showNoResults()
    }

    private fun showErrorView(message: String?) {
        empty_view.visibility = View.VISIBLE
        empty_view.showError(message)
    }

    private fun hideViews() {
        empty_view.visibility = View.GONE
    }
}