package de.visualdigits.newshomereader.data.model.applicationjson

import androidx.compose.runtime.Immutable
import de.visualdigits.newshomereader.data.serializer.AuthorWrapperSerializer
import kotlinx.serialization.Serializable

@Serializable(with = AuthorWrapperSerializer::class)
@Immutable
data class AuthorWrapper(
    val autors: List<AuthorDto> = listOf()
)
