package com.faraz.app.imagecheck

import com.google.gson.annotations.SerializedName


data class ImageModel(
        @SerializedName("resources") val resources: List<Resource>,
        @SerializedName("updated_at") val updatedAt: String
)

data class Resource(
        @SerializedName("public_id") val publicId: String,
        @SerializedName("version") val version: Int,
        @SerializedName("format") val format: String,
        @SerializedName("width") val width: Int,
        @SerializedName("height") val height: Int,
        @SerializedName("type") val type: String,
        @SerializedName("created_at") val createdAt: String
)

