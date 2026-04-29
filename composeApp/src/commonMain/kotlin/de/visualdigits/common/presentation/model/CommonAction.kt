package de.visualdigits.common.presentation.model

import androidx.compose.runtime.Immutable

sealed interface CommonAction {

    @Immutable
    data class OnScrollPositionChange(
        val id: String,
        val position: Int,
        val offset: Int? = null
    ): CommonAction
}
