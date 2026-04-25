package de.visualdigits.newshomereader.domain.repository

import de.visualdigits.newshomereader.domain.model.catalog.NewsFeedCatalog
import de.visualdigits.newshomereader.domain.model.errorhandling.DataError
import de.visualdigits.newshomereader.domain.model.errorhandling.Result

interface CatalogRepository {

    suspend fun loadCatalog(): Result<NewsFeedCatalog, DataError.Local>
}
