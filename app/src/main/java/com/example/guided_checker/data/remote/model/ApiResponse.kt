package com.example.guided_checker.data.remote.model

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    @SerializedName("message")
    val message: String?,
    @SerializedName("data")
    val data: T
)

data class MahasiswaWithStatus (
    @SerializedName("npm")
    val npm: String,
    @SerializedName("nama")
    val nama: String,
    @SerializedName("kelas")
    val kelas: String,
    @SerializedName("status")
    val status: StatusPresensi?
)

data class StatusPresensi (
    @SerializedName("id")
    val id: Int,
    @SerializedName("id_mahasiswa")
    val idMahasiswa: String, // npm
    @SerializedName("modul")
    val modul: String,
    @SerializedName("status")
    val status: String
)