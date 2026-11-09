package com.example.colecaojogos

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.colecaojogos.data.GameRepository
import com.example.colecaojogos.databinding.ActivityMainBinding
import com.example.colecaojogos.model.Game
import com.example.colecaojogos.ui.GameAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

/**
 * Tela principal: lista os jogos (READ), abre o formulário para
 * cadastrar (CREATE) ou editar (UPDATE) e permite excluir (DELETE).
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var repository: GameRepository
    private lateinit var adapter: GameAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        repository = GameRepository(this)

        adapter = GameAdapter(
            onItemClick = { game -> openForm(game.id) },
            onDeleteClick = { game -> confirmDelete(game) }
        )
        binding.recyclerGames.layoutManager = LinearLayoutManager(this)
        binding.recyclerGames.adapter = adapter

        binding.fabAdd.setOnClickListener { openForm(null) }
    }

    override fun onResume() {
        super.onResume()
        // Recarrega ao voltar do formulário, refletindo inclusões e edições.
        loadGames()
    }

    private fun loadGames() {
        val games = repository.getAll()
        adapter.submitList(games)
        binding.textEmpty.isVisible = games.isEmpty()
    }

    private fun openForm(gameId: Long?) {
        val intent = Intent(this, FormActivity::class.java)
        if (gameId != null) {
            intent.putExtra(FormActivity.EXTRA_GAME_ID, gameId)
        }
        startActivity(intent)
    }

    private fun confirmDelete(game: Game) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.delete_title)
            .setMessage(getString(R.string.delete_message, game.title))
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.delete) { _, _ ->
                repository.delete(game.id)
                loadGames()
                Snackbar.make(binding.root, R.string.game_deleted, Snackbar.LENGTH_SHORT).show()
            }
            .show()
    }

    override fun onDestroy() {
        repository.close()
        super.onDestroy()
    }
}
