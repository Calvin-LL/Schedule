package com.shortstack.hackertracker.ui.speakers

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.analytics.AnalyticsController
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.FirebaseSpeaker
import com.shortstack.hackertracker.ui.activities.MainActivity
import com.shortstack.hackertracker.views.EventView
import com.shortstack.hackertracker.views.StatusBarSpacer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_speakers.*
import javax.inject.Inject
import kotlin.math.absoluteValue


/**
 * Created by Chris on 7/31/2018.
 */
class SpeakerFragment : Fragment() {
    companion object {

        const val EXTRA_SPEAKER = "EXTRA_SPEAKER"

        fun newInstance(speaker: FirebaseSpeaker): SpeakerFragment {
            val fragment = SpeakerFragment()

            val bundle = Bundle()
            bundle.putParcelable(EXTRA_SPEAKER, speaker)
            fragment.arguments = bundle

            return fragment
        }
    }

    @Inject
    lateinit var database: DatabaseManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_speakers, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        App.application.component.inject(this)

        val context = context ?: return


        val drawable = ContextCompat.getDrawable(context, R.drawable.ic_arrow_back_white_24dp)
        toolbar.navigationIcon = drawable

        toolbar.setNavigationOnClickListener {
            (activity as? MainActivity)?.popBackStack()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val height = StatusBarSpacer.getStatusBarHeight(context, app_bar)
            app_bar.setPadding(0, height, 0, 0)
        }


        val speaker = arguments?.getParcelable(EXTRA_SPEAKER) as? FirebaseSpeaker
        speaker?.let {
            AnalyticsController.log("Viewing speaker ${it.name}")

            collapsing_toolbar.title = it.name
            collapsing_toolbar.subtitle = if (speaker.title.isEmpty()) {
                context.getString(R.string.speaker_default_title)
            } else {
                speaker.title
            }

            val url = speaker.twitter
            if (url.isEmpty()) {
                twitter.visibility = View.GONE
            } else {
                twitter.visibility = View.VISIBLE

                twitter.setOnClickListener {
                    val url = "https://twitter.com/" + url.replace("@", "")

                    val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse(url))
                    context.startActivity(intent)

                    AnalyticsController.onSpeakerEvent(AnalyticsController.SPEAKER_TWITTER, speaker)
                }
            }


            description.text = it.description

            val colours = context.resources.getStringArray(R.array.colors)
            val color = Color.parseColor(colours[speaker.hashCode().absoluteValue % colours.size])

            app_bar.setBackgroundColor(color)


            database.getEventsForSpeaker(it)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { list ->

                        list.forEach {
                            events.addView(EventView(context, it))
                        }
                    }

            AnalyticsController.onSpeakerEvent(AnalyticsController.SPEAKER_VIEW, it)
        }
    }
}