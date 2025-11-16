package com.pokedev.app.helpers

import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import coil.load
import com.pokedev.app.R
import com.pokedev.app.domain.Pokemon
import com.pokedev.app.domain.PokemonDetailResponse

@BindingAdapter("imageUrl")
fun setImageUrl(view: ImageView, url: String?) {
    if (url.isNullOrEmpty()) {
        view.setImageResource(R.drawable.ic_pokemon_placeholder)
        return
    }
    view.load(url) {
        crossfade(true)
        placeholder(R.drawable.ic_pokemon_placeholder)
        error(R.drawable.ic_error)
    }
}

@BindingAdapter("pokemonTypes")
fun setPokemonTypes(view: TextView, pokemonDetail: PokemonDetailResponse?) {
    pokemonDetail?.let {
        val types = it.types.joinToString(", ") { type ->
            type.type.name.replaceFirstChar { char ->
                if (char.isLowerCase()) char.titlecase() else char.toString()
            }
        }
        view.text = types
    }
}

@BindingAdapter("pokemonHeight")
fun setPokemonHeight(view: TextView, height: Int?) {
    height?.let {
        val heightInMeters = it / 10.0
        view.text = String.format("%.1f m", heightInMeters)
    }
}

@BindingAdapter("pokemonWeight")
fun setPokemonWeight(view: TextView, weight: Int?) {
    weight?.let {
        val weightInKg = it / 10.0
        view.text = String.format("%.1f kg", weightInKg)
    }
}

@BindingAdapter("pokemonStat")
fun setPokemonStat(view: TextView, pokemonDetail: PokemonDetailResponse?) {
    val statName = view.tag as? String ?: return

    pokemonDetail?.let {
        val stat = it.stats.find { statItem ->
            statItem.stat.name == statName
        }
        view.text = stat?.baseStat?.toString() ?: "0"
    }
}

@BindingAdapter("pokemonListTypes")
fun setPokemonListTypes(view: TextView, pokemon: Pokemon?) {
    val types = pokemon?.categories?.takeIf { it.isNotEmpty() }
    if (types.isNullOrEmpty()) {
        view.text = view.context.getString(R.string.pokemon_type_unknown)
        return
    }

    val formatted = types.joinToString(", ") { typeName ->
        typeName.replaceFirstChar { char ->
            if (char.isLowerCase()) char.titlecase() else char.toString()
        }
    }
    view.text = formatted
}


@BindingAdapter("backgroundColorFromPokemon")
fun View.setBackgroundColorFromPokemon(pokemon: Pokemon?) {
   
    if (pokemon == null || pokemon.categories.isEmpty()) {
        setBackgroundColor(ContextCompat.getColor(context, android.R.color.background_light))
        return
    }

    val primaryType = pokemon.categories.first().lowercase()

    val colorRes = when (primaryType) {
        "fire" -> R.color.type_fire
        "water" -> R.color.type_water
        "grass" -> R.color.type_grass
        "electric" -> R.color.type_electric
        "ice" -> R.color.type_ice
        "fighting" -> R.color.type_fighting
        "poison" -> R.color.type_poison
        "ground" -> R.color.type_ground
        "flying" -> R.color.type_flying
        "psychic" -> R.color.type_psychic
        "bug" -> R.color.type_bug
        "rock" -> R.color.type_rock
        "ghost" -> R.color.type_ghost
        "dragon" -> R.color.type_dragon
        "dark" -> R.color.type_dark
        "steel" -> R.color.type_steel
        "fairy" -> R.color.type_fairy
        else -> R.color.type_normal
    }

    val baseColor = ContextCompat.getColor(context, colorRes)
    val semiTransparent = Color.argb(38, Color.red(baseColor), Color.green(baseColor), Color.blue(baseColor))
    setBackgroundColor(semiTransparent)
}
