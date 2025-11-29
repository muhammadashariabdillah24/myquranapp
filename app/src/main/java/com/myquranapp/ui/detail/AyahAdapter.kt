package com.myquranapp.ui.detail

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.myquranapp.R
import com.myquranapp.core.domain.model.AppSettings
import com.myquranapp.core.domain.model.Ayah
import com.myquranapp.databinding.ItemAyahBinding

class AyahAdapter(
    private val onPlayClick: (String) -> Unit,
    private val onStopClick: () -> Unit
) : ListAdapter<Ayah, AyahAdapter.AyahViewHolder>(DIFF_CALLBACK) {

    private var currentPlayingUrl: String? = null
    private var settings: AppSettings = AppSettings()

    fun setCurrentPlayingUrl(url: String?) {
        val previousUrl = currentPlayingUrl
        currentPlayingUrl = url
        
        // Notify item changed for previous and current playing items
        if (previousUrl != null) {
            val previousIndex = currentList.indexOfFirst { it.recitationAudio == previousUrl }
            if (previousIndex != -1) {
                notifyItemChanged(previousIndex)
            }
        }
        
        if (url != null) {
            val currentIndex = currentList.indexOfFirst { it.recitationAudio == url }
            if (currentIndex != -1) {
                notifyItemChanged(currentIndex)
            }
        }
    }
    
    fun updateSettings(newSettings: AppSettings) {
        settings = newSettings
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AyahViewHolder {
        val binding = ItemAyahBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AyahViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AyahViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AyahViewHolder(
        private val binding: ItemAyahBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(ayah: Ayah) {
            binding.apply {
                // Ayah number
                tvAyahNumber.text = ayah.sequenceSurah.toString()
                
                // Arabic text with dynamic font size
                tvArabic.text = ayah.text
                tvArabic.setTextSize(TypedValue.COMPLEX_UNIT_SP, settings.arabicFontSize.toFloat())
                
                // Transliteration (Latin) - show/hide based on settings
                tvTransliteration.text = ayah.transliteration
                tvTransliteration.visibility = if (settings.showTransliteration) View.VISIBLE else View.GONE
                tvTransliteration.setTextSize(TypedValue.COMPLEX_UNIT_SP, settings.translationFontSize.toFloat())
                
                // Translation with dynamic font size
                tvTranslation.text = ayah.translation
                tvTranslation.setTextSize(TypedValue.COMPLEX_UNIT_SP, settings.translationFontSize.toFloat())

                // Play/Stop button
                val isPlaying = currentPlayingUrl == ayah.recitationAudio
                btnPlay.setImageResource(
                    if (isPlaying) R.drawable.ic_stop else R.drawable.ic_play
                )
                
                btnPlay.setOnClickListener {
                    if (isPlaying) {
                        onStopClick()
                    } else {
                        onPlayClick(ayah.recitationAudio)
                    }
                }
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Ayah>() {
            override fun areItemsTheSame(oldItem: Ayah, newItem: Ayah): Boolean {
                return oldItem.sequenceQuran == newItem.sequenceQuran
            }

            override fun areContentsTheSame(oldItem: Ayah, newItem: Ayah): Boolean {
                return oldItem == newItem
            }
        }
    }
}
