package com.advice.core.local

import android.os.Parcelable
import com.advice.core.utils.Time
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.util.Date

data class LocationRow(
    val id: Long,
    val title: String,
    val status: LocationStatus,
    val depth: Int,
    val hasChildren: Boolean,
    val isExpanded: Boolean,
)



@Parcelize
data class Location(
    val id: Long,
    val name: String,
    val shortName: String?,
    val hotel: String?,
    val conference: String,
    // Schedule
    val defaultStatus: String? = null,
    val depth: Int = -1,
    val hierExtentLeft: Int = -1,
    val hierExtentRight: Int = -1,
    val parent: Long = -1,
    val peerSortOrder: Int = -1,
    val schedule: List<LocationSchedule>? = null,
    val children: List<Location> = emptyList(),
    var isVisible: Boolean = true,
) : Parcelable {

    val hasChildren: Boolean
        get() = children.isNotEmpty()

    val status: LocationStatus
        get() {
            if (!hasChildren) {
                return getCurrentStatus()
            }

            val childrenStatus = children.map { it.status }
            return when {
                childrenStatus.all { it == LocationStatus.Open } -> LocationStatus.Open
                childrenStatus.all { it == LocationStatus.Closed } -> LocationStatus.Closed
                childrenStatus.all { it == LocationStatus.Unknown } -> LocationStatus.Unknown
                else -> LocationStatus.Mixed
            }
        }

    val isExpanded: Boolean
        get() {
            if (!hasChildren) {
                return false
            }
            return children.map { it.isVisible }.any()
        }

    private fun getCurrentStatus(): LocationStatus {
        val now = Time.now()

        val status = schedule?.firstOrNull {
            val begin = parse(it.begin)
            val end = parse(it.end)
            begin != null && end != null && begin.before(now) && end.after(now)
        }?.status ?: defaultStatus

        return when (status) {
            "open" -> LocationStatus.Open
            "closed" -> LocationStatus.Closed
            else -> LocationStatus.Unknown
        }
    }

    private fun parse(date: String): Date? {
        return try {
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(date)
        } catch (ex: Exception) {
            null
        }
    }

    override fun toString(): String {
        var s =  "  " + "--".repeat(depth - 1) + "> $name - isExpanded: $isExpanded, isVisible: $isVisible"
//        children.map {
//            s = "$s\n  $it"
//        }
        return s
    }
}