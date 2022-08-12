package com.advice.schedule.models.local

import android.graphics.Color
import android.os.Parcelable
import com.advice.schedule.utilities.Time
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
data class LocationContainer(
    val id: Long,
    val title: String,
    val shortTitle: String?,
    val defaultStatus: String?,
    val depth: Int,
    val schedule: List<LocationSchedule>,
    val isChildrenExpanded: Boolean = true,
    val isExpanded: Boolean = true,
    val hasChildren: Boolean = false
) : Parcelable {

    var status: LocationStatus = LocationStatus.Closed

    fun getCurrentStatus(): LocationStatus {
        val now = Time.now()

        val status = schedule.firstOrNull {
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
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+00:00")
            simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC")
            simpleDateFormat.parse(date)
        } catch (ex: Exception) {
            null
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other is LocationContainer) {
            return this.id == other.id
        }
        return super.equals(other)
    }
}

fun LocationContainer.isExpanded(isExpanded: Boolean): LocationContainer {
    val status = status
    return copy(isExpanded = isExpanded).apply {
        setStatus(status)
    }
}

fun LocationContainer.isChildrenExpanded(isExpanded: Boolean): LocationContainer {
    val status = status
    return copy(isChildrenExpanded = isExpanded).apply {
        setStatus(status)
    }
}

fun LocationContainer.hasChildren(hasChildren: Boolean) = copy(hasChildren = hasChildren)

fun LocationContainer.setStatus(status: LocationStatus): LocationContainer {
    this.status = status
    return this
}

fun LocationStatus.toColour(): Int = when (this) {
    LocationStatus.Open -> Color.GREEN
    LocationStatus.Closed -> Color.RED
    LocationStatus.Mixed -> Color.YELLOW
    LocationStatus.Unknown -> Color.GRAY
}