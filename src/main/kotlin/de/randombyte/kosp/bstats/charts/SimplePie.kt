package de.randombyte.kosp.bstats.charts

import com.google.gson.JsonObject

class SimplePie(chartId: String, val getValue: () -> String?) : CustomChart(chartId) {
    override fun getChartData(): JsonObject? = JsonObject().apply {
        val value = getValue()
        if (value == null || value.isEmpty()) return null
        addProperty("value", value)
    }
}