package com.advice.core.local

import android.os.Parcelable
import com.advice.core.utils.Time
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.util.Date

@Parcelize
data class LocationContainer(
    val id: Long,
    val title: String,
    val shortTitle: String?,
    val defaultStatus: String?,
    val depth: Int,
    val schedule: List<LocationSchedule>,
    val children: List<Location>,
    val isExpanded: Boolean = true,
    val isVisible: Boolean = true,

    ) : Parcelable {

    val hasChildren: Boolean
        get() = children.isNotEmpty()
    
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
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(date)
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

fun LocationContainer.isVisible(isVisible: Boolean): LocationContainer {
    val status = status
    return copy(isVisible = isVisible).apply {
        setStatus(status)
    }
}

fun LocationContainer.isChildrenExpanded(isExpanded: Boolean): LocationContainer {
    val status = status
    return copy(isExpanded = isExpanded).apply {
        setStatus(status)
    }
}

fun LocationContainer.setStatus(status: LocationStatus): LocationContainer {
    this.status = status
    return this
}