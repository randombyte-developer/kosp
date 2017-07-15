package de.randombyte.kosp.bstats.charts

import com.google.gson.JsonObject

class MultiLineChart(chartId: String, val getValues: () -> Map<String, Int>?) : CustomChart(chartId) {
    override fun getChartData(): JsonObject? = JsonObject().apply {
        val values = getValues() ?: return null
        val validatedValues = values.filterNot { it.value == 0 }
        if (validatedValues.isEmpty()) return null

        add("values", JsonObject().apply {
            validatedValues.forEach { addProperty(it.key, it.value) }
        })
    }
}