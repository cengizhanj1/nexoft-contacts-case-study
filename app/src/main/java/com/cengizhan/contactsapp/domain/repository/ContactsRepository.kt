package com.cengizhan.contactsapp.domain.repository

import com.cengizhan.contactsapp.domain.model.Contact

interface ContactsRepository {

    // Tüm kişileri listele
    suspend fun getContacts(): List<Contact>

    // Yeni kişi oluştur
    suspend fun createContact(contact: Contact): Contact

    // Kişi güncelle
    suspend fun updateContact(contact: Contact): Contact

    // Kişi sil
    suspend fun deleteContact(id: String)

    // Fotoğraf yükle, geriye server'dan dönen imageUrl'i verir
    suspend fun uploadImage(imageBytes: ByteArray, fileName: String): String
}
