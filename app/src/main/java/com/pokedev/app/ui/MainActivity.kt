package com.pokedev.app.ui

import android.content.Intent  // Import para navegação
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.pokedev.app.R
import com.pokedev.app.databinding.ActivityMainBinding
import com.pokedev.app.presentation.PokemonListViewModel
import com.pokedev.app.list.PokemonAdapter
import com.pokedev.app.domain.Generation

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: PokemonListViewModel
    private val tag = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this

        viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application))[PokemonListViewModel::class.java]
        binding.viewModel = viewModel

        // MODIFICADO: Adapter agora recebe callback de clique
        val adapter = PokemonAdapter { pokemon ->
            // Navegar para DetailActivity ao clicar
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("pokemon", pokemon)
            startActivity(intent)
        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.pokemonListLiveData.observe(this) { pokemonList ->
            adapter.submitList(pokemonList)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.lottieLoading.visibility = View.VISIBLE
                binding.lottieLoading.playAnimation()
            } else {
                binding.lottieLoading.pauseAnimation()
                binding.lottieLoading.visibility = View.GONE
            }
        }

        viewModel.errorMessage.observe(this) { message ->
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }

        Log.d(tag, "onCreate chamado")

        binding.searchEditText.requestFocus()

        setupSearch()
        setupTypeFilter()
        setupGenerationFilter()
    }

    override fun onStart() { super.onStart(); Log.d(tag, "onStart chamado") }
    override fun onResume() { super.onResume(); Log.d(tag, "onResume chamado") }
    override fun onPause() { super.onPause(); Log.d(tag, "onPause chamado") }
    override fun onStop() { super.onStop(); Log.d(tag, "onStop chamado") }
    override fun onDestroy() { super.onDestroy(); Log.d(tag, "onDestroy chamado") }
    override fun onRestart() { super.onRestart(); Log.d(tag, "onRestart chamado") }

    private fun setupSearch() {
        binding.searchEditText.doOnTextChanged { text, _, _, _ ->
            viewModel.updateSearchQuery(text?.toString().orEmpty())
        }
    }

    private fun setupTypeFilter() {
        val typeAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.type_filter_options,
            android.R.layout.simple_spinner_item
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        binding.typeSpinner.adapter = typeAdapter
        binding.typeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selected = parent.getItemAtPosition(position) as String
                val normalizedType = when (selected) {
                    getString(R.string.filter_type_all) -> null
                    else -> selected.lowercase()
                }
                viewModel.updateTypeFilter(normalizedType)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                viewModel.updateTypeFilter(null)
            }
        }
    }

    private fun setupGenerationFilter() {
        val generationAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.generation_filter_options,
            android.R.layout.simple_spinner_item
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        binding.generationSpinner.adapter = generationAdapter
        binding.generationSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selected = parent.getItemAtPosition(position) as String
                val generation = when (selected) {
                    getString(R.string.filter_generation_gen_i) -> Generation.GEN_I
                    getString(R.string.filter_generation_gen_ii) -> Generation.GEN_II
                    else -> null
                }
                viewModel.updateGenerationFilter(generation)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                viewModel.updateGenerationFilter(null)
            }
        }
    }
}
