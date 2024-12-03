package karlhto.routes.v1

import io.ktor.server.plugins.NotFoundException
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import karlhto.repository.CountryRepository
import karlhto.repository.Region
import karlhto.utils.SortBy
import karlhto.utils.SortOrder
import karlhto.utils.sortCountries

fun Route.countryRoutesV1(repository: CountryRepository) {
    route("/v1/countries") {
        get("/{region}") {
            val region = Region.fromString(call.parameters["region"])
                ?: throw NotFoundException("Region not found")

            val sortBy = call.request.queryParameters["sort_by"]?.let { SortBy.valueOf(it.uppercase()) }
            val sortOrder = call.request.queryParameters["sort_order"]?.let { SortOrder.valueOf(it.uppercase()) }

            val countries = sortCountries(
                repository.getCountriesByRegion(region), sortBy, sortOrder
            )

            call.respond(countries)
        }
    }
    route("/v1/currencies") {
        get {
            val currencies = repository.getAllCurrenciesWithCountries()
            call.respond(currencies)
        }
    }
}
