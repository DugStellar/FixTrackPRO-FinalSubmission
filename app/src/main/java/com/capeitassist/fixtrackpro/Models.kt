package com.capeitassist.fixtrackpro

data class LoginResponse(
    val status: String,
    val message: String
)

data class RegisterResponse(
    val status: String,
    val message: String
)
data class ReportModel(
    val id: Int,
    val title: String,
    val category: String,
    val status: String,
    val createdAt: String
)