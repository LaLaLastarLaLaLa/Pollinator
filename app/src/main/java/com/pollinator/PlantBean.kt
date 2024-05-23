package com.pollinator

class PlantBean(
    val date: String,
    val time: String,
    val family: String,
    val genus: String,
    val species: String,
    val openLocationCode: OpenLocationCode,
    val altitude: Int,
    val accuracy: Int
) {
}