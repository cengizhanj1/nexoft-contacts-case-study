package com.cengizhan.contactsapp.presentation.add_edit

sealed class AddEditContactEvent {
    data class FirstNameChanged(val value: String) : AddEditContactEvent()
    data class LastNameChanged(val value: String) : AddEditContactEvent()
    data class PhoneNumberChanged(val value: String) : AddEditContactEvent()
    data class ProfileImageUrlChanged(val value: String) : AddEditContactEvent()
    object SaveClicked : AddEditContactEvent()
}
