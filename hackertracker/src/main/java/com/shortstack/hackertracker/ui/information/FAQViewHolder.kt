package com.shortstack.hackertracker.ui.information

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.crashlytics.android.answers.CustomEvent
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.analytics.AnalyticsController
import com.shortstack.hackertracker.models.FAQ
import kotlinx.android.synthetic.main.row_faq.view.*

class FAQViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    fun inflate(inflater: LayoutInflater, parent: ViewGroup): View {
        return inflater.inflate(R.layout.row_faq, parent, false)
    }

    fun render(faq: FAQ) {
        view.answer.visibility = View.GONE

        view.question.text = faq.question
        view.answer.text = faq.answer

        view.setOnClickListener {
            onFAQClick(faq)
        }
    }

    private fun onFAQClick(faq: FAQ) {
        val root = view.container

        val isExpanded = view.answer.visibility == View.VISIBLE

        if (!isExpanded) {
            val event = CustomEvent(AnalyticsController.FAQ_VIEW).also {
                it.putCustomAttribute("Question", faq.question)
            }
            AnalyticsController.logCustom(event)
        }


        val visibility = if (isExpanded) View.GONE else View.VISIBLE

        val constraintSet1 = ConstraintSet()
        constraintSet1.clone(root)

        constraintSet1.setVisibility(R.id.answer, visibility)

        val transition = ChangeBounds()
        transition.interpolator = AccelerateDecelerateInterpolator()


        TransitionManager.beginDelayedTransition(root, transition)
        constraintSet1.applyTo(root)
    }
}
