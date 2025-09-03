package com.example.adminwavesoffood.Model

data class UserModel(
    val email : String? = null,
    val password : String? = null,
    val name : String? = null,
    val nameOfRestaurant : String? = null,
    var phone : String ?= null,
    var address : String ?= null
)
