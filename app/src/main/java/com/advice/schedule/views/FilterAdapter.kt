package com.advice.schedule.views

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.advice.schedule.models.firebase.FirebaseTag
import com.advice.schedule.models.local.Location
import com.advice.schedule.ui.schedule.LocationViewHolder
import com.advice.schedule.ui.schedule.TypeViewHolder
import com.advice.schedule.ui.search.HeaderViewHolder

// todo: replace with ListAdapter
class FilterAdapter(
    private val onClickListener: (FirebaseTag) -> Unit,
    private val onLongClickListener: (FirebaseTag) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val collection = ArrayList<Any>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> HeaderViewHolder.inflate(parent)
            TYPE_TAG -> TypeViewHolder.inflate(parent)
            TYPE_LOCATION -> LocationViewHolder.inflate(parent)
            else -> error("unknown viewType: $viewType")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (collection[position]) {
            is String -> TYPE_HEADER
            is FirebaseTag -> TYPE_TAG
            is Location -> TYPE_LOCATION
            else -> error("unknown type: ${collection[position].javaClass}")
        }
    }

    override fun getItemCount() = collection.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> holder.render(collection[position] as String)
            is TypeViewHolder -> holder.render(
                collection[position] as FirebaseTag,
                onClickListener,
                onLongClickListener
            )
            is LocationViewHolder -> holder.render(collection[position] as Location)
        }
    }

    fun setElements(elements: List<Any>) {
        collection.clear()
        collection.addAll(elements)
        notifyDataSetChanged()
    }

    companion object {
        const val TYPE_HEADER = 0
        const val TYPE_TAG = 1
        const val TYPE_LOCATION = 2
    }
}