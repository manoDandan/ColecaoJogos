package com.example.colecaojogos

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.colecaojogos.data.GameRepository
import com.example.colecaojogos.databinding.ActivityFormBinding
import com.example.colecaojogos.model.Game
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import java.util.Calendar

/**
 * Formulário usado tanto para cadastrar um novo jogo quanto para
 * editar um existente (quando recebe EXTRA_GAME_ID).
 */
class FormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormBinding
    private lateinit var repository: GameRepository

    private var editingGame: Game? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = GameRepository(this)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        (binding.inputPlatform as MaterialAutoCompleteTextView).setSimpleItems(R.array.platforms)
        (binding.inputGenre as MaterialAutoCompleteTextView).setSimpleItems(R.array.genres)

        val gameId = intent.getLongExtra(EXTRA_GAME_ID, NO_ID)
        if (gameId != NO_ID) {
            loadGame(gameId)
        } else {
            supportActionBar?.setTitle(R.string.new_game)
        }

        binding.buttonSave.setOnClickListener { saveGame() }
        binding.buttonCancel.setOnClickListener { finish() }
    }

    private fun loadGame(id: Long) {
        val game = repository.getById(id)
        if (game == null) {
            Toast.makeText(this, R.string.game_not_found, Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        editingGame = game
        supportActionBar?.setTitle(R.string.edit_game)

        binding.inputTitle.setText(game.title)
        binding.inputPlatform.setText(game.platform, false)
        binding.inputGenre.setText(game.genre, false)
        binding.inputYear.setText(game.year.toString())
        binding.ratingBar.rating = game.rating
        binding.checkCompleted.isChecked = game.completed
    }

    private fun saveGame() {
        if (!validate()) return

        val game = Game(
            id = editingGame?.id ?: 0,
            title = binding.inputTitle.text.toString().trim(),
            platform = binding.inputPlatform.text.toString().trim(),
            genre = binding.inputGenre.text.toString().trim(),
            year = binding.inputYear.text.toString().trim().toInt(),
            rating = binding.ratingBar.rating,
            completed = binding.checkCompleted.isChecked
        )

        if (editingGame == null) {
            repository.insert(game)
            Toast.makeText(this, R.string.game_saved, Toast.LENGTH_SHORT).show()
        } else {
            repository.update(game)
            Toast.makeText(this, R.string.game_updated, Toast.LENGTH_SHORT).show()
        }
        finish()
    }

    private fun validate(): Boolean {
        var valid = true
        val required = getString(R.string.error_required)

        binding.layoutTitle.error = null
        binding.layoutPlatform.error = null
        binding.layoutGenre.error = null
        binding.layoutYear.error = null

        if (binding.inputTitle.text.isNullOrBlank()) {
            binding.layoutTitle.error = required
            valid = false
        }
        if (binding.inputPlatform.text.isNullOrBlank()) {
            binding.layoutPlatform.error = required
            valid = false
        }
        if (binding.inputGenre.text.isNullOrBlank()) {
            binding.layoutGenre.error = required
            valid = false
        }

        val year = binding.inputYear.text.toString().trim().toIntOrNull()
        val maxYear = Calendar.getInstance().get(Calendar.YEAR) + 1
        if (year == null || year !in MIN_YEAR..maxYear) {
            binding.layoutYear.error = getString(R.string.error_year, MIN_YEAR, maxYear)
            valid = false
        }
        return valid
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onDestroy() {
        repository.close()
        super.onDestroy()
    }

    companion object {
        const val EXTRA_GAME_ID = "extra_game_id"
        private const val NO_ID = -1L
        private const val MIN_YEAR = 1950
    }
}
