package com.example.hairsalonappointments.data

/**
 * Additional data classes for extended features
 */

data class StylistStats(
    val stylistName: String,
    val appointmentCount: Int,
    val totalRevenue: Double,
    val averageRating: Float,
    val specialties: List<ServiceType>
)

data class ServiceStats(
    val serviceType: ServiceType,
    val bookingCount: Int,
    val totalRevenue: Double,
    val averageDuration: Int,
    val popularityRank: Int
)

data class DailyRevenue(
    val date: String,
    val totalRevenue: Double,
    val revenueByService: Map<ServiceType, Double>,
    val revenueByStylist: Map<String, Double>,
    val appointmentCount: Int
)

data class Stylist(
    val id: Int,
    val name: String,
    val specialties: List<ServiceType>,
    val yearsExperience: Int,
    val rating: Float,
    val bio: String
)

data class TimeSlot(
    val time: String,
    val available: Boolean,
    val stylistName: String? = null
)

data class ClientHistoryItem(
    val appointment: Appointment,
    val rating: Int? = null,
    val feedback: String? = null
)