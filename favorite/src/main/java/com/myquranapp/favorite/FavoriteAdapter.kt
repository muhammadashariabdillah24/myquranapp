package com.myquranapp.favorite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.myquranapp.core.domain.model.Surah
import com.myquranapp.favorite.databinding.ItemFavoriteBinding

class FavoriteAdapter(
    private val onItemClick: (Surah) -> Unit
) : ListAdapter<Surah, FavoriteAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFavoriteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemFavoriteBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(surah: Surah) {
            binding.apply {
                tvSurahNumber.text = surah.sequence.toString()
                tvSurahName.text = surah.name.latinShort
                tvSurahArabic.text = surah.name.arabicShort
                tvSurahTranslation.text = surah.translation
                tvAyahCount.text = root.context.getString(
                    com.myquranapp.favorite.R.string.ayah_count_format,
                    surah.ayahCount
                )
                tvSurahType.text = surah.type.latin

                root.setOnClickListener {
                    onItemClick(surah)
                }
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Surah>() {
            override fun areItemsTheSame(oldItem: Surah, newItem: Surah): Boolean {
                return oldItem.sequence == newItem.sequence
            }

            override fun areContentsTheSame(oldItem: Surah, newItem: Surah): Boolean {
                return oldItem == newItem
            }
        }
    }
}
