package com.pokedev.app.ui

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.pokedev.app.R
import com.pokedev.app.databinding.ActivityDetailBinding
import com.pokedev.app.domain.Pokemon
import android.view.View
import com.pokedev.app.presentation.DetailViewModel
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var viewModel: DetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail)
        binding.lifecycleOwner = this

        viewModel = ViewModelProvider(this)[DetailViewModel::class.java]
        binding.viewModel = viewModel

        // ✅ Declare 'pokemon' aqui (fora do if)
        val pokemon: Pokemon? = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("pokemon", Pokemon::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("pokemon")
        }

        // ✅ Agora 'pokemon' está visível aqui
        pokemon?.let { binding.pokemon = it }

        // Buscar dados detalhados da API
        pokemon?.let {
            viewModel.fetchPokemonDetails(it.number)
        }

        // Observer para os detalhes do Pokemon
        viewModel.pokemonDetail.observe(this) { pokemonDetail ->
            pokemonDetail?.let {
                binding.pokemonDetail = it
                binding.executePendingBindings()
            }
        }

        // Observer para estado de loading
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observer para mensagens de erro
        viewModel.errorMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }

        // Botão Voltar
        binding.btnBack.setOnClickListener {
            finish() // Volta para a MainActivity
        }
    }
}