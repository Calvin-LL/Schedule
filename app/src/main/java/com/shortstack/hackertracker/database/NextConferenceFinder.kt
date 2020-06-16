package com.shortstack.hackertracker.database

import com.shortstack.hackertracker.models.local.Conference

object NextConferenceFinder {

    fun getNext(conferences: List<Conference>, selected: Int = -1): Conference? {
        if (conferences.isEmpty())
            return null

        if (selected != -1) {
            val previous = conferences.find { it.id == selected }
            if (previous?.hasFinished == false) {
                return previous
            }
        }

        val defcon = conferences.find { it.code == "DEFCON28" }
        if (defcon?.hasFinished == false) {
            return defcon
        }

        val sorted = conferences.sortedBy { it.startDate }

        val active = sorted.firstOrNull { !it.hasFinished }
        if (active != null) {
            return active
        }

        return sorted.last()
    }
}