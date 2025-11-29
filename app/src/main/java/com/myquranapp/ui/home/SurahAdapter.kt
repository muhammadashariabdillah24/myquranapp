package com.myquranapp.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.myquranapp.R
import com.myquranapp.core.domain.model.Surah
import com.myquranapp.databinding.ItemSurahBinding

class SurahAdapter(
    private val onItemClick: (Surah) -> Unit,
    private val onFavoriteClick: (Surah, Boolean) -> Unit
) : ListAdapter<Surah, SurahAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSurahBinding.inflate(
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
        private val binding: ItemSurahBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(surah: Surah) {
            binding.apply {
                tvSurahNumber.text = surah.sequence.toString()
                tvSurahName.text = surah.name.latinShort
                tvSurahArabic.text = surah.name.arabicShort
                tvSurahTranslation.text = surah.translation
                tvAyahCount.text = root.context.getString(R.string.ayah_count, surah.ayahCount)
                tvSurahType.text = surah.type.latin

                // Set favorite icon (will be implemented when we add favorite feature)
                // For now, we'll use a simple click listener
                
                root.setOnClickListener {
                    onItemClick(surah)
                }

                // Favorite button will be added later
                // btnFavorite.setOnClickListener { 
                //     onFavoriteClick(surah, !surah.isFavorite) 
                // }
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
