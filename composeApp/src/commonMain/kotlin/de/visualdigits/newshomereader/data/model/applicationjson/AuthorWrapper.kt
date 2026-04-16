package de.visualdigits.newshomereader.data.model.applicationjson

import de.visualdigits.newshomereader.data.serializer.AuthorWrapperSerializer
import kotlinx.serialization.Serializable

@Serializable(with = AuthorWrapperSerializer::class)
data class AuthorWrapper(
    val autors: List<AuthorDto> = listOf()
)
