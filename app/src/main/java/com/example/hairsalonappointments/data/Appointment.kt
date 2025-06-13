package com.example.hairsalonappointments.data

import java.util.Date

data class Appointment(
    val id: Int,
    val clientName: String,
    val clientPhone: String,
    val stylistName: String,
    val serviceType: ServiceType,
    val appointmentTime: Date,
    val status: AppointmentStatus,
    val notes: String? = null
)

enum class AppointmentStatus {
    CONFIRMED,
    PENDING,
    COMPLETED,
    CANCELLED
}