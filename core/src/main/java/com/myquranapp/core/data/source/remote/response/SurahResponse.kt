package com.myquranapp.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class SurahResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("data")
    val data: List<SurahItem>
)

data class SurahItem(
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
    
    @SerializedName("recitation")
    val recitation: RecitationResponse
)

data class TypeResponse(
    @SerializedName("arabic")
    val arabic: String,
    
    @SerializedName("latin")
    val latin: String
)

data class NameResponse(
    @SerializedName("arabic")
    val arabic: ArabicNameResponse,
    
    @SerializedName("latin")
    val latin: LatinNameResponse
)

data class ArabicNameResponse(
    @SerializedName("long")
    val long: String,
    
    @SerializedName("short")
    val short: String
)

data class LatinNameResponse(
    @SerializedName("long")
    val long: String,
    
    @SerializedName("short")
    val short: String
)

data class RecitationResponse(
    @SerializedName("audio")
    val audio: String
)
