package com.cengizhan.contactsapp.presentation.add_edit

data class AddEditContactUiState(
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val profileImageUrl: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSaved: Boolean = false
)
