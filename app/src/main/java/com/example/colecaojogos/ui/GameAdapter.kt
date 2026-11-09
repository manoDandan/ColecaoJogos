package com.example.colecaojogos.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.colecaojogos.R
import com.example.colecaojogos.databinding.ItemGameBinding
import com.example.colecaojogos.model.Game

/**
 * Adapter do RecyclerView. Usa ListAdapter + DiffUtil para atualizar
 * a lista de forma eficiente sempre que o CRUD altera os dados.
 */
class GameAdapter(
    private val onItemClick: (Game) -> Unit,
    private val onDeleteClick: (Game) -> Unit
) : ListAdapter<Game, GameAdapter.GameViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val binding = ItemGameBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GameViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class GameViewHolder(private val binding: ItemGameBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(game: Game) {
            val context = binding.root.context
            binding.textTitle.text = game.title
            binding.textInfo.text =
                context.getString(R.string.info_format, game.platform, game.genre, game.year)
            binding.textRating.text = context.getString(R.string.rating_format, game.rating)
            binding.textStatus.text = context.getString(
                if (game.completed) R.string.status_completed else R.string.status_not_completed
            )

            binding.root.setOnClickListener { onItemClick(game) }
            binding.buttonDelete.setOnClickListener { onDeleteClick(game) }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<Game>() {
        override fun areItemsTheSame(oldItem: Game, newItem: Game) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Game, newItem: Game) = oldItem == newItem
    }
}
