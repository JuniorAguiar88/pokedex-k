package com.pokedev.app.domain

import com.google.gson.annotations.SerializedName

data class PokemonDetailResponse(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val sprites: Sprites,
    val stats: List<StatItem>,
    val types: List<TypeItem>
)

data class Sprites(
    @SerializedName("front_default")
    val frontDefault: String?
)

data class StatItem(
    @SerializedName("base_stat")
    val baseStat: Int,
    val stat: StatName
)

data class StatName(
    val name: String
)

data class TypeItem(
    val type: TypeName
)

data class TypeName(
    val name: String
)




