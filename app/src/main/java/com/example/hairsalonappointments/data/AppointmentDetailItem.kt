package com.example.hairsalonappointments.data

import com.example.hairsalonappointments.data.Appointment

sealed class AppointmentDetailItem {
    data class AppointmentSummary(val title: String) : AppointmentDetailItem()
    data class ClientDetails(val clientName: String, val clientPhone: String) : AppointmentDetailItem()
    data class ServiceDetails(val serviceType: ServiceType, val stylistName: String) : AppointmentDetailItem()
    data class AppointmentTimeDetails(val appointment: Appointment) : AppointmentDetailItem()
    data class NotesDetails(val notes: String?) : AppointmentDetailItem()
    data class PreviousVisitsHeader(val title: String) : AppointmentDetailItem()
    data class ClientHistory(val clientHistory: Appointment) : AppointmentDetailItem()
    data class NoPreviousVisits(val message: String) : AppointmentDetailItem()
}