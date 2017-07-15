package de.randombyte.kosp.bstats.charts

import com.google.gson.JsonObject

class SingleLineChart(chartId: String, val getValue: () -> Int?) : CustomChart(chartId) {
    override fun getChartData(): JsonObject? = JsonObject().apply {
        val value = getValue()
        if (value == null || value == 0) return null
        addProperty("value", value)
    }
}