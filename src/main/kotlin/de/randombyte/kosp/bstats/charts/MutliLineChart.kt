package de.randombyte.kosp.bstats.charts

import com.google.gson.JsonObject

abstract class MutliLineChart(chartId: String) : CustomChart<Map<String, Int>?>(chartId) {
    override fun getChartData(): JsonObject? = JsonObject().apply {
        val values = getValue() ?: return null
        val validatedValues = values.filterNot { it.value == 0 }
        if (validatedValues.isEmpty()) return null

        add("values", JsonObject().apply {
            validatedValues.forEach { addProperty(it.key, it.value) }
        })
    }
}