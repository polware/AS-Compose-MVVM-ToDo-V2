package com.polware.todov2compose.data.models

import androidx.compose.ui.graphics.Color
import com.polware.todov2compose.ui.theme.HighPriority
import com.polware.todov2compose.ui.theme.LowPriority
import com.polware.todov2compose.ui.theme.MediumPriority
import com.polware.todov2compose.ui.theme.NonePriority

enum class Priority(val color: Color) {
    HIGH(HighPriority),
    MEDIUM(MediumPriority),
    LOW(LowPriority),
    NONE(NonePriority)
}