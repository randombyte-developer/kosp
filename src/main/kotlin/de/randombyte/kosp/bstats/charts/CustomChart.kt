package de.randombyte.kosp.bstats.charts

import com.google.gson.JsonObject

abstract class CustomChart(protected val chartId: String) {
    fun getRequestedJsonObject(): JsonObject? {
        val data = getChartData() ?: return null
        return JsonObject().apply {
            addProperty("chartId", chartId)
            add("data", data)
        }
    }

    protected abstract fun getChartData(): JsonObject?
}