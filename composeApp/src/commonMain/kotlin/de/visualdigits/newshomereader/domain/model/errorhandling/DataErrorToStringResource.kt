package de.visualdigits.newshomereader.domain.model.errorhandling

import de.visualdigits.common.domain.model.ui.UiText
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.error_local_disk_full
import de.visualdigits.compose.resources.error_local_file_not_found
import de.visualdigits.compose.resources.error_local_serialization
import de.visualdigits.compose.resources.error_local_unknown
import de.visualdigits.compose.resources.error_remote_no_internet
import de.visualdigits.compose.resources.error_remote_request_timeout
import de.visualdigits.compose.resources.error_remote_serialization
import de.visualdigits.compose.resources.error_remote_unknown
import de.visualdigits.compose.resources.error_unknown_field

fun DataError.toUiText(): UiText {
    val stringResource = when (this) {

        DataError.Local.FILE_NOT_FOUND -> Res.string.error_local_file_not_found
        DataError.Local.SERIALIZATION -> Res.string.error_local_serialization
        DataError.Local.DISK_FULL -> Res.string.error_local_disk_full
        DataError.Local.UNKNOWN_FIELD -> Res.string.error_unknown_field
        DataError.Local.UNKNOWN -> Res.string.error_local_unknown

        DataError.Remote.REQUEST_TIMEOUT -> Res.string.error_remote_request_timeout
        DataError.Remote.NO_INTERNET -> Res.string.error_remote_no_internet
        DataError.Remote.SERVER -> Res.string.error_remote_unknown
        DataError.Remote.SERIALIZATION -> Res.string.error_remote_serialization
        DataError.Remote.UNKNOWN -> Res.string.error_remote_unknown
    }

    return UiText.StringResourceId(stringResource)
}
