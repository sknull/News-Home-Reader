package de.visualdigits.newshomereader.data.model.applicationjson

import androidx.compose.runtime.Immutable
import de.visualdigits.newshomereader.data.serializer.AuthorWrapperSerializer
import de.visualdigits.newshomereader.data.serializer.ListSerializer
import kotlinx.serialization.Serializable

@Serializable(with = AuthorWrapperSerializer::class)
@Immutable
data class AuthorWrapper(
    @Serializable(with = ListSerializer::class) val autors: List<AuthorDto> = listOf()
)
