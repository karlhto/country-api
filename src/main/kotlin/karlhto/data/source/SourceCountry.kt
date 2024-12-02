package karlhto.data.source

import kotlinx.serialization.Serializable

@Serializable
data class SourceCountry(
    val name: SourceCountryName,
    val region: String,
    val currencies: Map<String, SourceCurrency>
) {
    @Serializable
    data class SourceCountryName(val common: String)

    @Serializable
    data class SourceCurrency(val name: String, val symbol: String)
}
