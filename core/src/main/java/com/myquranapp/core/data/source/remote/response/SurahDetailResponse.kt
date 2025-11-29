package com.myquranapp.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class SurahDetailResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("data")
    val data: SurahDetailItem
)

data class SurahDetailItem(
    @SerializedName("sequence")
    val sequence: Int,
    
    @SerializedName("ayahCount")
    val ayahCount: Int,
    
    @SerializedName("type")
    val type: TypeResponse,
    
    @SerializedName("name")
    val name: NameResponse,
    
    @SerializedName("translation")
    val translation: String,
    
    @SerializedName("preBismillah")
    val preBismillah: BismillahResponse,
    
    @SerializedName("recitation")
    val recitation: RecitationResponse,
    
    @SerializedName("ayah")
    val ayah: List<AyahResponse>
)

data class BismillahResponse(
    @SerializedName("text")
    val text: String,
    
    @SerializedName("translation")
    val translation: String,
    
    @SerializedName("transliteration")
    val transliteration: String
)

data class AyahResponse(
    @SerializedName("sequence")
    val sequence: SequenceResponse,
    
    @SerializedName("juz")
    val juz: Int,
    
    @SerializedName("page")
    val page: Int,
    
    @SerializedName("text")
    val text: String,
    
    @SerializedName("transliteration")
    val transliteration: String,
    
    @SerializedName("translation")
    val translation: String,
    
    @SerializedName("recitation")
    val recitation: RecitationResponse
)

data class SequenceResponse(
    @SerializedName("quran")
    val quran: Int,
    
    @SerializedName("surah")
    val surah: Int
)
