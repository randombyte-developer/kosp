package de.randombyte.kosp.bstats.charts

import com.google.gson.JsonObject

class SimpleBarChart(chartId: String, val getValues: () -> Map<String, Int>?) : CustomChart(chartId) {
    override fun getChartData(): JsonObject? = JsonObject().apply {
        val values = getValues()
        if (values == null || values.isEmpty()) return null
        add("values", JsonObject().apply {
            values.forEach { addProperty(it.key, it.value) }
        })
    }
}