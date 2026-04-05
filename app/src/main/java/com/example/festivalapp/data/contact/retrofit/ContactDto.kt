package com.example.festivalapp.data.contact.retrofit

import com.example.festivalapp.data.contact.room.Contact
import kotlinx.serialization.Serializable

@Serializable
data class ContactDto(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String? = null,
    val role: String? = null,
    val priority: Boolean = false,
    val idEditor: Int
) {
    fun toRoom(): Contact = Contact(
        id = id,
        name = name,
        email = email,
        phone = phone,
        role = role,
        priority = priority,
        idEditor = idEditor
    )
}
