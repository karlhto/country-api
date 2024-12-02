package karlhto.repository

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import karlhto.mock.RestCountriesMockResponse
import kotlin.test.Test
import kotlin.test.assertEquals

class CountryRepositoryTest {
    private val responseHeaders = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))

    private val mockEngine = MockEngine { request: HttpRequestData ->
        respond(
            content = RestCountriesMockResponse.response,
            status = HttpStatusCode.OK,
            headers = responseHeaders
        )
    }

    val repository = CountryRepository(mockEngine)

    @Test
    fun `should generate a list of countries based on region`() {
        val countries = repository.getCountriesByRegion(Region.EUROPE)
        assertEquals(25, countries.size)
        assertEquals(false, countries.any { it.name == "Afghanistan" })
    }

    @Test
    fun `should generate a list of all currencies with countries`() {
        val currenciesWithCountries = repository.getAllCurrenciesWithCountries()
        assertEquals(12, currenciesWithCountries.size)
        assertEquals(
            listOf(
                "Åland Islands", "Andorra", "Austria", "Belgium", "Cyprus", "Estonia", "Finland", "France", "Germany",
                "Greece", "Ireland", "Italy", "Latvia", "Lithuania", "Luxembourg", "Malta", "Monaco", "Netherlands",
                "Portugal", "San Marino", "Slovakia", "Slovenia", "Spain", "Vatican City"
            ), currenciesWithCountries["EUR"]
        )
        assertEquals(listOf("Afghanistan"), currenciesWithCountries["AFN"])
        assertEquals(listOf("Albania"), currenciesWithCountries["ALL"])
        assertEquals(listOf("Brazil"), currenciesWithCountries["BRL"])
        assertEquals(listOf("Canada"), currenciesWithCountries["CAD"])
        assertEquals(listOf("China"), currenciesWithCountries["CNY"])
        assertEquals(listOf("India"), currenciesWithCountries["INR"])
        assertEquals(listOf("Japan"), currenciesWithCountries["JPY"])
        assertEquals(listOf("Mexico"), currenciesWithCountries["MXN"])
        assertEquals(listOf("Nigeria"), currenciesWithCountries["NGN"])
        assertEquals(listOf("South Africa"), currenciesWithCountries["ZAR"])
        assertEquals(listOf("United States"), currenciesWithCountries["USD"])
    }

    @Test
    fun `should use underlying cache, resulting in only one call to API on multiple requests`() {
        var requestCount = 0
        val newMockEngine = MockEngine { request: HttpRequestData ->
            requestCount++
            respond(
                content = RestCountriesMockResponse.response,
                status = HttpStatusCode.OK,
                headers = responseHeaders
            )
        }
        val newRepository = CountryRepository(newMockEngine)

        val firstCallCountries = newRepository.getCountriesByRegion(Region.EUROPE)
        val secondCallCountries = newRepository.getCountriesByRegion(Region.EUROPE)

        assertEquals(1, requestCount)
        assertEquals(firstCallCountries, secondCallCountries)
    }
}
