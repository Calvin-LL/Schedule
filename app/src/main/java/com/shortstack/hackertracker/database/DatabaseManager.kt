package com.shortstack.hackertracker.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.Query
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.storage.FirebaseStorage
import com.orhanobut.logger.Logger
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.BuildConfig
import com.shortstack.hackertracker.models.firebase.*
import com.shortstack.hackertracker.models.local.*
import com.shortstack.hackertracker.network.task.ReminderWorker
import com.shortstack.hackertracker.utilities.MyClock
import com.shortstack.hackertracker.utilities.Storage
import com.shortstack.hackertracker.utilities.now
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.concurrent.TimeUnit


class DatabaseManager(private val preferences: Storage) {

    companion object {
        private const val CONFERENCES = "conferences"

        private const val USERS = "users"
        private const val BOOKMARKS = "bookmarks"

        private const val EVENTS = "events"
        private const val TYPES = "types"
        private const val FAQS = "faqs"
        private const val VENDORS = "vendors"
        private const val SPEAKERS = "speakers"
        private const val LOCATIONS = "locations"
        private const val ARTICLES = "articles"
    }

    private val code
        get() = conference.value?.code ?: "DEFCON27"

    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()


    val conference = MutableLiveData<Conference>()
    val conferences = MutableLiveData<List<Conference>>()

    init {
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(!BuildConfig.DEBUG)
            .build()
    }

    suspend fun init() {
        val snapshot = firestore.collection(CONFERENCES)
            .get()
            .await()

        val list = snapshot.toObjects(FirebaseConference::class.java)
            .filter { !it.hidden || App.isDeveloper }
            .map { it.toLocal() }
            .sortedBy { it.startDate }

        val con = NextConferenceFinder.getNext(list, preferences.preferredConference)
        conference.postValue(con)
        conferences.postValue(list)

        if (con != null)
            getFCMToken(con)
    }


    private fun getFCMToken(conference: Conference) {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Logger.e(task.exception, "Could not get token.")
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token
                Logger.d("Obtained token: $token")
                updateFirebaseMessagingToken(conference, token)
            })
    }

    fun changeConference(id: Int) {
        preferences.preferredConference = id

        val current = conference.value

        if (current != null) {
            current.isSelected = false
        }

        firestore.collection(CONFERENCES)
            .whereEqualTo("id", id)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val selected =
                        it.result?.toObjects(FirebaseConference::class.java)?.firstOrNull()
                            ?.toLocal()
                    conference.postValue(selected)
                }
            }
    }

    fun getConferences(): LiveData<List<Conference>> {
        val mutableLiveData = MutableLiveData<List<Conference>>()

        firestore.collection(CONFERENCES)
            .addSnapshotListener { snapshot, exception ->
                if (exception == null) {
                    val cons = snapshot?.toObjects(FirebaseConference::class.java)
                        ?.filter { !it.hidden || App.isDeveloper }
                        ?.map { it.toLocal() }

                    mutableLiveData.postValue(cons)
                }
            }

        return mutableLiveData
    }

    fun getSchedule(conference: Conference): LiveData<List<Event>> {
        return firestore.getCollection<FirebaseEvent, Event>(conference, EVENTS)
    }

    fun getBookmarks(conference: Conference, user: String): LiveData<List<Bookmark>> {
        return firestore.getUserCollection<FirebaseBookmark, Bookmark>(conference, user, BOOKMARKS)
    }

    fun getTypes(id: Conference): LiveData<List<Type>> {
        return firestore.getCollection<FirebaseType, Type>(id, TYPES)
    }

    fun getRecent(): LiveData<List<Event>> {
        val mutableLiveData = MutableLiveData<List<Event>>()

        firestore.collection(CONFERENCES)
            .document(code)
            .collection(EVENTS)
            .orderBy("updated_timestamp", Query.Direction.DESCENDING)
            .limit(5)
            .get()
            .addOnSuccessListener {
                val events = it.toObjects(FirebaseEvent::class.java)
                    .filter { !it.hidden || App.isDeveloper }
                    .map { it.toLocal() }

                mutableLiveData.postValue(events)
            }

        return mutableLiveData
    }

    fun getArticles(conference: Conference): LiveData<List<Article>> {
        return firestore.getCollection<FirebaseArticle, Article>(conference, ARTICLES)
    }

    fun getFAQ(id: Conference): LiveData<List<FAQ>> {
        return firestore.getCollection<FirebaseFAQ, FAQ>(id, FAQS)
    }

    fun getLocations(conference: Conference): LiveData<List<Location>> {
        return firestore.getCollection<FirebaseLocation, Location>(conference, LOCATIONS)
    }

    fun getVendors(conference: Conference): LiveData<List<Vendor>> {
        return firestore.getCollection<FirebaseVendor, Vendor>(conference, VENDORS)
    }

    suspend fun getEventById(conference: String, id: Int): Event? {
        val snapshot = firestore.collection(CONFERENCES)
            .document(conference)
            .collection(EVENTS)
            .document(id.toString())
            .get()
            .await()

        return snapshot.toObject(FirebaseEvent::class.java)?.toLocal()
    }

    fun updateBookmark(event: Event) {
        val tag = "reminder_" + event.id

        if (event.isBookmarked) {
            val delay = event.start.time - MyClock().now().time - (1000 * 20 * 60)

            if (delay > 0) {
                val data = workDataOf(
                    ReminderWorker.INPUT_ID to event.id,
                    ReminderWorker.INPUT_CONFERENCE to event.conference
                )

                val notify = OneTimeWorkRequestBuilder<ReminderWorker>()
                    .setInputData(data)
                    .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                    .addTag(tag)
                    .build()

                WorkManager.getInstance().enqueue(notify)
            }

        } else {
            WorkManager.getInstance().cancelAllWorkByTag(tag)
        }
    }

    fun updateTypeIsSelected(type: Type) {

    }

    private fun updateFirebaseMessagingToken(conference: Conference?, token: String?) {

    }

    fun clear() {

    }


    fun getSpeakers(conference: Conference): LiveData<List<Speaker>> {
        return firestore.getCollection<FirebaseSpeaker, Speaker>(conference, SPEAKERS)
    }

    fun getEventsForSpeaker(speaker: Speaker): LiveData<List<Event>> {
        val mutableLiveData = MutableLiveData<List<Event>>()

        firestore.collection(CONFERENCES)
            .document(code)
            .collection(EVENTS)
            .addSnapshotListener { snapshot, exception ->
                if (exception == null) {
                    val events = snapshot?.toObjects(FirebaseEvent::class.java)
                    val filtered =
                        events?.filter { it.speakers.firstOrNull { it.id == speaker.id } != null }
                            ?.map { it.toLocal() }
                    mutableLiveData.postValue(filtered)
                }
            }

        return mutableLiveData
    }


    fun getMaps(conference: Conference): MutableLiveData<List<FirebaseConferenceMap>> {
        val mutableLiveData = MutableLiveData<List<FirebaseConferenceMap>>()

        val list = ArrayList<FirebaseConferenceMap>()

        val maps = conference.maps
        if (maps.isEmpty()) {
            return mutableLiveData
        }

        maps.forEach {
            val map = FirebaseConferenceMap(it.name, it.file, null)
            list.add(map)
        }

        mutableLiveData.postValue(list)

        list.forEach {
            val filename = "${conference.code}-${it.path}"
            val file = File(App.instance.applicationContext.getExternalFilesDir(null), filename)
            if (file.exists()) {
                it.file = file
            } else {
                file.createNewFile()

                val map = storage.reference.child("/${conference.code}/${it.path}")

                map.getFile(file).addOnSuccessListener { task ->
                    it.file = file

                    mutableLiveData.postValue(list.toList())
                }.addOnFailureListener {
                    // Handle any errors
                }
            }
        }
        return mutableLiveData
    }
}