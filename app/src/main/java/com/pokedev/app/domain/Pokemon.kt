package com.pokedev.app.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Pokemon(
    val number: Int,
    val title: String,
    val sprite: String,
    val categories: List<String>,
    val evolution: Generation
) : Parcelable