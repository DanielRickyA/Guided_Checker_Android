package com.example.guided_checker.data.remote.api

import com.example.guided_checker.data.remote.model.ApiResponse
import com.example.guided_checker.data.remote.model.MahasiswaWithStatus
import com.example.guided_checker.data.remote.model.StatusPresensi
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @GET("Mahasiswa/{kelas}/{modul}")
    suspend fun getMahasiswa(
        @Path("kelas") kelas: String,
        @Path("modul") modul: String
    ): ApiResponse<List<MahasiswaWithStatus>>

    @POST("status")
    @FormUrlEncoded
    suspend fun addStatus(
        @Field("id_mahasiswa") idMahasiswa: String,
        @Field("modul") modul: String,
        @Field("status") status: String = "1"
    ): ApiResponse<StatusPresensi>

    @DELETE("status/{id}")
    suspend fun deleteStatus(
        @Path("id") id: Int
    ): ApiResponse<StatusPresensi>
}