package de.randombyte.kosp.bstats.charts

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive

class SimpleBarChart(chartId: String, val getValues: () -> Map<String, Int>?) : CustomChart(chartId) {
    override fun getChartData(): JsonObject? = JsonObject().apply {
        val values = getValues()
        if (values == null || values.isEmpty()) return null

        add("values", JsonObject().apply {
            values.forEach { (key, value) ->
                add(key, JsonArray().apply {
                    add(JsonPrimitive(value))
                })
            }
        })
    }
}