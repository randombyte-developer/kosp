package de.randombyte.kosp.bstats.charts

import com.google.gson.JsonObject

abstract class SimplePie(chartId: String) : CustomChart<String?>(chartId) {
    override fun getChartData(): JsonObject? = JsonObject().apply {
        val value = getValue()
        if (value == null || value.isEmpty()) return null
        addProperty("value", value)
    }
}