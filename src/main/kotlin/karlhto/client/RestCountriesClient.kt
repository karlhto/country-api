package karlhto.client

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.logging.KtorSimpleLogger
import karlhto.data.source.SourceCountry
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit

/**
 * A client for fetching country data from the RestCountries API, with internal cache.
 *
 * The cache invalidates after 1 hour.
 */
class RestCountriesClient(
    engine: HttpClientEngine
) {
    private val logger = KtorSimpleLogger(this::class.qualifiedName!!)
    private val client = HttpClient(engine) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    private val mutex = Mutex()
    private var countryCache = emptyList<SourceCountry>()
    private var lastCacheTime = 0L

    /**
     * This property ensures that the cache is populated and up-to-date before returning the list.
     */
    val sourceCountryList: List<SourceCountry>
        get() = runBlocking {
            mutex.withLock {
                if (countryCache.isEmpty() || isCacheExpired()) {
                    populateCache()
                }
                countryCache
            }
        }

    private suspend fun populateCache() {
        val response = client.get("https://restcountries.com/v3.1/all?fields=name,currencies,region")
        if (response.status != HttpStatusCode.OK) {
            if (countryCache.isNotEmpty()) {
                logger.warn("Failed to fetch data from ${response.request.url}, continuing to use cached data")
                return
            }
            throw IllegalStateException("Failed to fetch data from ${response.request.url}")
        }

        val countries: List<SourceCountry> = response.body()
        countryCache = countries
        lastCacheTime = System.currentTimeMillis()
    }

    private fun isCacheExpired() = (System.currentTimeMillis() - lastCacheTime) > TimeUnit.HOURS.toMillis(1)
}
