package de.visualdigits.common.domain.model

import org.jetbrains.compose.resources.DrawableResource

interface StringResourceEnumerable<T : StringResourceEnumerable<T>> : Enumerable<T> {

    val uiText: UiText
    val drawableResourceId: DrawableResource?
}
