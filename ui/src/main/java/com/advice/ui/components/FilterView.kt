package com.advice.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring.StiffnessLow
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.advice.core.local.Tag
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme
import com.advice.ui.utils.createTag
import com.advice.ui.utils.parseColor
import kotlin.math.min

@Composable
internal fun FilterView(tag: Tag, onClick: () -> Unit) {
    val alpha = remember {
        Animatable(0f)
    }

    LaunchedEffect(key1 = tag.isSelected, block = {
        alpha.animateTo(if (tag.isSelected) 1f else 0f)
    })

    val tagColor = parseColor(tag.color)

    Row(
        Modifier
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        AnimatedCircleTextView(selected = tag.isSelected, text = tag.label, color = tagColor)
    }
}

@Composable
fun AnimatedCircleTextView(selected: Boolean, text: String, color: Color) {
    val transition = updateTransition(targetState = selected, label = "CircleTransition")

    val circleSize = transition.animateDp(
        transitionSpec = {
            if (false isTransitioningTo true) {
                spring(stiffness = StiffnessLow)
            } else {
                spring(stiffness = StiffnessLow)
            }
        },
        label = "CircleSizeTransition"
    ) { isSelected ->
        if (isSelected) Dp.Infinity else 4.dp
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp)
            .drawBehind {
                val radius = min(size.width, size.height) / 2
                val circleSizePixels = circleSize.value.toPx()

                if (circleSizePixels > radius) {
                    drawRoundRect(color = color, cornerRadius = CornerRadius(12.dp.toPx()))
                } else {
                    drawCircle(
                        color = color,
                        center = Offset(x = circleSizePixels, y = size.height / 2),
                        radius = circleSizePixels,
                    )
                }
            }
            .padding(4.dp), contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}


@LightDarkPreview
@Composable
private fun FilterViewPreview() {
    ScheduleTheme {
        Column {
            FilterView(createTag(label = "Talk", color = "#FF0066")) {

            }
            FilterView(createTag(label = "Event", color = "#FF0066", isSelected = true)) {

            }
        }

    }
}