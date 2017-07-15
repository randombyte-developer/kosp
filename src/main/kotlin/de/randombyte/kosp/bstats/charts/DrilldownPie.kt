package de.randombyte.kosp.bstats.charts

import com.google.gson.JsonObject

class DrilldownPie(chartId: String, val getValues: () -> Map<String, Map<String, Int>>?) : CustomChart(chartId) {
    override fun getChartData(): JsonObject? = JsonObject().apply {
        val values = getValues() ?: return null
        val validatedValues = values.filterNot { (_, valuesMap) -> valuesMap.isEmpty() }
        if (validatedValues.isEmpty()) return null

        add("values", JsonObject().apply {
            validatedValues.forEach { (key, valueMap) ->
                add(key, JsonObject().apply {
                    valueMap.forEach { (valuesMapKey, value) ->
                        addProperty(valuesMapKey, value)
                    }
                })
            }
        })
    }
}