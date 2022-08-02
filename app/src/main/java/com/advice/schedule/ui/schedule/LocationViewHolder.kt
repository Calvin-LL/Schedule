package com.advice.schedule.ui.schedule

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.advice.schedule.models.local.Location
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.databinding.ItemTypeBinding

class LocationViewHolder(private val binding: ItemTypeBinding) : RecyclerView.ViewHolder(binding.root) {

    private val context = binding.root.context

    fun render(location: Location) = with(binding) {
        text.text = location.name

        val color = Color.parseColor("#FFFFFF")
        dot.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.chip_background_small)?.mutate()?.apply { setTint(color) })
        full.setImageDrawable(
            ContextCompat.getDrawable(context, R.drawable.chip_background_rounded)?.mutate()?.apply { setTint(color) })

        dot.visibility = if (!location.isSelected) View.VISIBLE else View.GONE
        full.visibility = if (location.isSelected) View.VISIBLE else View.GONE
    }

    companion object {
        fun inflate(parent: ViewGroup): LocationViewHolder {
            val binding =
                ItemTypeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return LocationViewHolder(binding)
        }
    }
}