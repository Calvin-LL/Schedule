package com.shortstack.hackertracker.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.firebase.FirebaseConference
import com.shortstack.hackertracker.models.firebase.FirebaseConferenceMap
import com.shortstack.hackertracker.models.local.*
import com.shortstack.hackertracker.ui.themes.ThemesManager
import com.shortstack.hackertracker.utilities.Storage
import org.koin.core.KoinComponent
import org.koin.core.inject

class HackerTrackerViewModel : ViewModel(), KoinComponent {

    private val database: DatabaseManager by inject()
    private val storage: Storage by inject()
    private val themes: ThemesManager by inject()


    // todo: pass in the right conference
    val conference: Conference = FirebaseConference(code = "DEFCON27").toLocal()


    val events: LiveData<List<Event>>
        get() = _events

    private val _events = MediatorLiveData<List<Event>>()

    val bookmarks: LiveData<List<Bookmark>>
        get() = _bookmarks

    private val _bookmarks = MediatorLiveData<List<Bookmark>>()


    val types: LiveData<List<Type>>
        get() = _types

    private val _types = MediatorLiveData<List<Type>>()

    val locations: LiveData<List<Location>>
        get() = _locations

    private val _locations = MediatorLiveData<List<Location>>()

    val speakers: LiveData<List<Speaker>>
        get() = _speakers

    private val _speakers = MediatorLiveData<List<Speaker>>()

    val articles: LiveData<List<Article>>
        get() = _articles

    private val _articles = MediatorLiveData<List<Article>>()

    val faq: LiveData<List<FAQ>>
        get() = _faq

    private val _faq = MediatorLiveData<List<FAQ>>()


    val vendors: LiveData<List<Vendor>>
        get() = _vendors

    private val _vendors = MediatorLiveData<List<Vendor>>()

    val maps: LiveData<List<FirebaseConferenceMap>>
        get() = _maps

    private val _maps = MediatorLiveData<List<FirebaseConferenceMap>>()

    // Home
    val home: LiveData<List<Any>>
        get() = _home

    private val _home = MediatorLiveData<List<Any>>()

    // Schedule
    val schedule: LiveData<List<Event>>
        get() = _schedule

    private val _schedule = MediatorLiveData<List<Event>>()


    // Search
    private val query = MediatorLiveData<String>()
    val search: LiveData<List<Any>>
        get() = _search

    private val _search = MediatorLiveData<List<Any>>()


    init {
        _types.addSource(database.getTypes(conference)) {
            _types.value = it
        }

        _locations.addSource(database.getLocations(conference)) {
            _locations.value = it
        }

        _events.addSource(database.getSchedule(conference)) {
            _events.value = it
        }

        _schedule.addSource(events) {
            val types = _types.value ?: emptyList()
            _schedule.value = getSchedule(it, types)
        }

        _schedule.addSource(types) {
            val events = _events.value ?: return@addSource
            _schedule.value = getSchedule(events, it)
        }

        // todo: get user id
        _bookmarks.addSource(database.getBookmarks(conference, "user-01")) {
            _bookmarks.value = it
        }

        _speakers.addSource(database.getSpeakers(conference)) {
            _speakers.value = it
        }

        _articles.addSource(database.getArticles(conference)) {
            _articles.value = it
        }

        _faq.addSource(database.getFAQ(conference)) {
            _faq.value = it
        }

        _vendors.addSource(database.getVendors(conference)) {
            _vendors.value = it
        }

        _maps.addSource(database.getMaps(conference)) {
            _maps.value = it
        }

        _articles.addSource(database.getArticles(conference)) {
            _articles.value = it
        }

        // todo: pass the search query
        _search.addSource(events) {
            val locations = locations.value ?: emptyList()
            val speakers = speakers.value ?: emptyList()
            _search.value = getSearchResults(
                "query",
                it ?: emptyList(),
                locations,
                speakers
            )
        }

        _search.addSource(locations) {
            val events = events.value ?: emptyList()
            val speakers = speakers.value ?: emptyList()
            _search.value = getSearchResults(
                "query",
                events,
                it ?: emptyList(),
                speakers
            )
        }

        _search.addSource(speakers) {
            val events = events.value ?: emptyList()
            val locations = locations.value ?: emptyList()
            _search.value = getSearchResults(
                "query",
                events,
                locations,
                it ?: emptyList()
            )
        }

        _home.addSource(bookmarks) {
            val articles = articles.value?.take(4) ?: emptyList()
            _home.value = articles + (it?.take(3) ?: emptyList())
        }

        _home.addSource(articles) {
            val bookmarks = bookmarks.value?.take(3) ?: emptyList()
            _home.value = it.take(4) + bookmarks
        }
    }

    private fun getSchedule(events: List<Event>, types: List<Type>): List<Event> {
        if (types.isEmpty())
            return events

        val requireBookmark = types.firstOrNull { it.isBookmark }?.isSelected ?: false
        val filter = types.filter { !it.isBookmark && it.isSelected }
        if (!requireBookmark && filter.isEmpty())
            return events

        if (requireBookmark && filter.isEmpty())
            return events.filter { it.isBookmarked }

        return events.filter { event -> isShown(event, requireBookmark, filter) }
    }

    private fun isShown(event: Event, requireBookmark: Boolean, filter: List<Type>): Boolean {
        val bookmark = if (requireBookmark) {
            event.isBookmarked
        } else {
            true
        }

        return bookmark && filter.find { it.id == event.type.id }?.isSelected == true
    }

    private fun getSearchResults(
        query: String,
        events: List<Event>,
        locations: List<Location>,
        speakers: List<Speaker>
    ): List<Any> {
        if (query.isBlank()) {
            return emptyList()
        }

        val list = ArrayList<Any>()

        val speakers = speakers.filter {
            it.name.contains(query, true) || it.description.contains(
                query,
                true
            )
        }
        if (speakers.isNotEmpty()) {
            list.add("Speakers")
            list.addAll(speakers)
        }

        val locations = locations.filter { it.name.contains(query, true) }
        locations.forEach { location ->
            list.add(location)
            // TODO: Should we add the filtered events, or all events for this location?
            list.addAll(events.filter { it.location.name == location.name }.sortedBy { it.start })
        }

        val events =
            events.filter { it.title.contains(query, true) || it.description.contains(query, true) }
        if (events.isNotEmpty()) {
            list.add("Events")
            list.addAll(events)
        }

        return list
    }


    fun onQueryTextChange(text: String?) {
        query.value = text
    }

}