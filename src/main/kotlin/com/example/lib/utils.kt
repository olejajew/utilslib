package com.example.lib

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun Instant.beautify(withSeconds: Boolean = true, zoneId: ZoneId = ZoneId.of("GMT+3")): String {
    val formatter = if (withSeconds) {
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    } else {
        DateTimeFormatter.ofPattern("yyyy-MM-dd")
    }
    val gmt3Zone = this.atZone(zoneId)
    return formatter.format(gmt3Zone)
}

fun String.escapeCsvValue(): String {
    return if (this.contains(",") || this.contains("\"") || this.contains("\n")) {
        "\"${this.replace("\"", "\"\"")}\""
    } else {
        this
    }
}

fun <T> ObjectMapper.readOrNull(json: String?, clazz: Class<T>): T? {
    json ?: return null
    return try {
        this.readValue(json, clazz)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun <T> ObjectMapper.readOrNull(json: String?, typeReference: TypeReference<T>): T? {
    json ?: return null
    return try {
        this.readValue(json, typeReference)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun JsonNode.getOrDefault(fieldName: String, defaultValue: String): String {
    return this.get(fieldName)?.asText() ?: defaultValue
}

fun randomString(length: Int = 12): String {
    val chars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    return (1..length)
        .map { chars.random() }
        .joinToString("")
}