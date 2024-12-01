package karlhto

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.request
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit

@Serializable(with = CountrySerializer::class)
data class Country(val name: String, val region, val currencies: List<String>)

class Client {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    private val mutex = Mutex()
    private var _cache = emptyList<Country>()
    private var lastCacheTime = 0L

    // TODO: Consider moving this to a separate class from data manipulation to limit direct access to cache
    private val countryList: List<Country>
        get() = runBlocking {
            mutex.withLock {
                if (_cache.isEmpty() || isCacheExpired()) {
                    populateCache()
                }
                _cache
            }
        }

    fun getCountriesByRegion(region: String) =
        countryList.filter { it.region == region }.map { Pair(it.name, it.currencies) }

    fun getSortedListOfCountries() =
        countryList.sortedBy { it.name }

    fun getListOfCurrenciesWithCountries() =
        countryList.flatMap { country ->
            country.currencies.map { currency -> Pair(currency, country.name) }
        }.groupBy({ it.first }, { it.second })

    private suspend fun populateCache() {
        val response = client.get("https://restcountries.com/v3.1/all?fields=name,currencies,region")
        if (response.status != HttpStatusCode.OK) {
            // If we have data in cache, we can ignore the error, but still try to fetch next time
            if (_cache.isNotEmpty()) {
                return
            }
            throw IllegalStateException("Failed to fetch data from ${response.request.url}")
        }

        val countries: List<Country> = Json.decodeFromString(response.toString())
        _cache = countries
        lastCacheTime = System.currentTimeMillis()
    }

    private fun isCacheExpired() = (System.currentTimeMillis() - lastCacheTime) > TimeUnit.HOURS.toMillis(1)
}