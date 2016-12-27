package de.randombyte.kosp.bstats.charts

import com.google.gson.JsonObject
import de.randombyte.kosp.bstats.Country

abstract class SimpleMapChart(chartId: String) : CustomChart<Country?>(chartId) {
    override fun getChartData(): JsonObject? = JsonObject().apply {
        val value = getValue() ?: return null
        addProperty("value", value.isoTag)
    }
}