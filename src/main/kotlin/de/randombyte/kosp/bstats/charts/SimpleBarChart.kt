package de.randombyte.kosp.bstats.charts

import com.google.gson.JsonObject

abstract class SimpleBarChart(chartId: String) : CustomChart<Map<String, Int>?>(chartId) {
    override fun getChartData(): JsonObject? = JsonObject().apply {
        val values = getValue()
        if (values == null || values.isEmpty()) return null
        add("values", JsonObject().apply {
            values.forEach { addProperty(it.key, it.value) }
        })
    }
}