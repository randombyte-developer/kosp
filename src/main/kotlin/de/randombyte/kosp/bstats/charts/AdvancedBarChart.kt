package de.randombyte.kosp.bstats.charts

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive

class AdvancedBarChart(chartId: String, val getValues: () -> Map<String, List<Int>>?) : CustomChart(chartId) {
    override fun getChartData(): JsonObject? = JsonObject().apply {
        val values = getValues() ?: return null
        val validatedValues = values.filterNot { it.value.isEmpty() }
        if (validatedValues.isEmpty()) return null

        add("values", JsonObject().apply {
            validatedValues.forEach {
                add(it.key, JsonArray().apply {
                    it.value.forEach { add(JsonPrimitive(it)) }
                })
            }
        })
    }
}