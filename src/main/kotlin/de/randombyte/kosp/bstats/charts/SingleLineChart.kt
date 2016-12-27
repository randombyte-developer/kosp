package de.randombyte.kosp.bstats.charts

import com.google.gson.JsonObject

abstract class SingleLineChart(chartId: String) : CustomChart<Int?>(chartId) {
    override fun getChartData(): JsonObject? = JsonObject().apply {
        val value = getValue()
        if (value == null || value == 0) return null
        addProperty("value", value)
    }
}