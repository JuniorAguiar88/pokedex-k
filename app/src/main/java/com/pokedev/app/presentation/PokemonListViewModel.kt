package com.pokedev.app.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokedev.app.api.RetrofitClient
import com.pokedev.app.domain.Pokemon
import com.pokedev.app.domain.Generation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PokemonListViewModel : ViewModel() {

    private val _pokemonListLiveData = MutableLiveData<List<Pokemon>>()
    val pokemonListLiveData: LiveData<List<Pokemon>> = _pokemonListLiveData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private var fullPokemonList: List<Pokemon> = emptyList()
    private var searchQuery: String = ""
    private var selectedTypeFilter: String? = null
    private var selectedGenerationFilter: Generation? = null

    init {
        loadPokemonFromApi()
    }

    fun updateSearchQuery(query: String) {
        searchQuery = query
        applyFilters()
    }

    fun updateTypeFilter(type: String?) {
        selectedTypeFilter = type?.lowercase()
        applyFilters()
    }

    fun updateGenerationFilter(generation: Generation?) {
        selectedGenerationFilter = generation
        applyFilters()
    }

    private fun loadPokemonFromApi() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val pokemonList = withContext(Dispatchers.IO) {
                    val response = RetrofitClient.pokeApiService.getPokemonList(limit = 251)
                    coroutineScope {
                        response.results.map { result ->
                            async {
                                val id = result.url.split("/").dropLast(1).last().toInt()
                                val detail = runCatching {
                                    RetrofitClient.pokeApiService.getPokemonById(id)
                                }.getOrNull()

                                val types = detail?.types?.map { it.type.name } ?: emptyList()
                                val imageUrl = detail?.sprites?.frontDefault
                                    ?: "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png"

                                Pokemon(
                                    number = id,
                                    title = formatName(result.name),
                                    sprite = imageUrl,
                                    categories = types,
                                    evolution = calculateGeneration(id)
                                )
                            }
                        }.awaitAll()
                    }.sortedBy { it.number }
                }

                fullPokemonList = pokemonList
                applyFilters()
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Sem conexão: ${e.message}"
                _pokemonListLiveData.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun applyFilters() {
        var filteredList = fullPokemonList

        if (searchQuery.isNotBlank()) {
            val queryLower = searchQuery.trim().lowercase()
            filteredList = filteredList.filter { it.title.lowercase().contains(queryLower) }
        }

        selectedTypeFilter?.let { type ->
            filteredList = filteredList.filter { pokemon ->
                pokemon.categories.any { it.equals(type, ignoreCase = true) }
            }
        }

        selectedGenerationFilter?.let { generation ->
            filteredList = filteredList.filter { it.evolution == generation }
        }

        _pokemonListLiveData.value = filteredList
    }

    private fun formatName(rawName: String): String {
        return rawName.replaceFirstChar { char ->
            if (char.isLowerCase()) char.titlecase() else char.toString()
        }
    }

    private fun calculateGeneration(id: Int): Generation {
        return when (id) {
            in 1..151 -> Generation.GEN_I
            in 152..251 -> Generation.GEN_II
            else -> Generation.OTHER
        }
    }
}
