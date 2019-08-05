package com.opentadam.ui.registration.api.model

// TODO: remove
data class ContactsDTO(val contacts: List<ContactDTO>?)

class ContactDTO {
    var cityName: String? = null
    val name: String? = null
    val email: String? = null
    val address: String? = null
    val gender: String? = null
    val phone: PhoneDTO? = null
}

class PhoneDTO {
    var mobile: String? = null
    var home: String? = null
    var office: String? = null
}