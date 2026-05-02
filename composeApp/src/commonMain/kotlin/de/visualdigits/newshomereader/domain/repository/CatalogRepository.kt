package de.visualdigits.newshomereader.domain.repository

import de.visualdigits.common.domain.model.errorhandling.Result
import de.visualdigits.newshomereader.domain.model.catalog.NewsFeedCatalog
import de.visualdigits.newshomereader.domain.model.errorhandling.DataError

interface CatalogRepository {

    suspend fun loadCatalog(): Result<NewsFeedCatalog, DataError.Local>
}
