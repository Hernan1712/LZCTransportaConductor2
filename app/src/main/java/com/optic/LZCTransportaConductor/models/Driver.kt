package com.optic.LZCTransportaConductor.models

import com.beust.klaxon.*

private val klaxon = Klaxon()

data class Driver (
    val id: String? = null,
    val name: String ? = null,
    val lastname: String ? = null,
    val email: String ? = null,
    var image: String ? = null,
    val plateNumber: String ? = null,
    var token: String? = null
) {
    fun toJson() = klaxon.toJsonString(this)

    companion object {
        fun fromJson(json: String) = klaxon.parse<Driver>(json)
    }
}
