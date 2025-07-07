package com.example.hairsalonappointments.data

import java.util.Calendar
import java.util.Date
import kotlin.random.Random

/**
 * Mock API Service for the Hair Salon Appointment App
 *
 * This service provides mock data for the application.
 * In a real application, this would make network requests to a backend API.
 */
class MockApiService {

    private val appointments = listOf(
        Appointment(
            id = 1,
            clientName = "Jennifer Martinez",
            clientPhone = "(555) 123-4567",
            stylistName = "Sarah Johnson",
            serviceType = ServiceType.CUT_AND_STYLE,
            appointmentTime = createAppointmentTime(9, 0),
            status = AppointmentStatus.COMPLETED,
            notes = "Regular client - prefers layers and face-framing highlights"
        ),
        Appointment(
            id = 2,
            clientName = "Michael Chen",
            clientPhone = "(555) 234-5678",
            stylistName = "Emma Wilson",
            serviceType = ServiceType.TRIM,
            appointmentTime = createAppointmentTime(9, 30),
            status = AppointmentStatus.COMPLETED,
            notes = null
        ),
        Appointment(
            id = 3,
            clientName = "Ashley Thompson",
            clientPhone = "(555) 345-6789",
            stylistName = "Sarah Johnson",
            serviceType = ServiceType.BALAYAGE,
            appointmentTime = createAppointmentTime(10, 0),
            status = AppointmentStatus.CONFIRMED,
            notes = "First time balayage - wants natural, sun-kissed look"
        ),
        Appointment(
            id = 4,
            clientName = "Robert Williams",
            clientPhone = "(555) 456-7890",
            stylistName = "Marcus Lee",
            serviceType = ServiceType.CUT_AND_STYLE,
            appointmentTime = createAppointmentTime(11, 30),
            status = AppointmentStatus.CONFIRMED,
            notes = "Allergic to certain hair products - use hypoallergenic only"
        ),
        Appointment(
            id = 5,
            clientName = "Sophia Rodriguez",
            clientPhone = "(555) 567-8901",
            stylistName = "Emma Wilson",
            serviceType = ServiceType.KERATIN_TREATMENT,
            appointmentTime = createAppointmentTime(12, 0),
            status = AppointmentStatus.PENDING,
            notes = "Needs to confirm arrival time - might be 15 mins late"
        ),
        Appointment(
            id = 6,
            clientName = "David Kim",
            clientPhone = "(555) 678-9012",
            stylistName = "Marcus Lee",
            serviceType = ServiceType.COLOR_TREATMENT,
            appointmentTime = createAppointmentTime(14, 30),
            status = AppointmentStatus.CONFIRMED,
            notes = null
        ),
        Appointment(
            id = 7,
            clientName = "Emily Davis",
            clientPhone = "(555) 789-0123",
            stylistName = "Jessica Brown",
            serviceType = ServiceType.HIGHLIGHTS,
            appointmentTime = createAppointmentTime(15, 0),
            status = AppointmentStatus.CONFIRMED,
            notes = "Wants platinum blonde highlights - discussed during consultation"
        ),
        Appointment(
            id = 8,
            clientName = "James Wilson",
            clientPhone = "(555) 890-1234",
            stylistName = "Marcus Lee",
            serviceType = ServiceType.DEEP_CONDITIONING,
            appointmentTime = createAppointmentTime(16, 30),
            status = AppointmentStatus.PENDING,
            notes = "Dry, damaged hair - recommended deep conditioning treatment"
        )
    )

    /**
     * Returns a list of today's appointments.
     *
     * @return List of appointments for today
     */
    fun getTodaysAppointments(): List<Appointment> {
        // Simulate a small delay that would occur with a real API call
        Thread.sleep(300)
        return appointments
    }

    /**
     * Returns a specific appointment by ID.
     *
     * @param id The appointment ID to search for
     * @return The appointment if found, null otherwise
     */
    fun getAppointmentById(id: Int): Appointment? {
        // Simulate a small delay that would occur with a real API call
        Thread.sleep(200)
        return appointments.find { it.id == id }
    }

    /**
     * Returns available time slots for booking.
     * This would typically check against existing appointments to find open slots.
     *
     * @return List of available time slots as strings
     */
    fun getAvailableSlots(): List<String> {
        // Simulate a small delay that would occur with a real API call
        Thread.sleep(250)

        // Generate all possible time slots (9 AM - 6 PM, 30-minute intervals)
        val allSlots = mutableListOf<String>()
        for (hour in 9..17) {
            allSlots.add(String.format("%d:00 %s", if (hour > 12) hour - 12 else hour, if (hour >= 12) "PM" else "AM"))
            if (hour < 17) {
                allSlots.add(String.format("%d:30 %s", if (hour > 12) hour - 12 else hour, if (hour >= 12) "PM" else "AM"))
            }
        }

        // Remove slots that have appointments
        val bookedTimes = appointments.map { appointment ->
            val calendar = Calendar.getInstance()
            calendar.time = appointment.appointmentTime
            val hour = calendar.get(Calendar.HOUR)
            val minute = calendar.get(Calendar.MINUTE)
            val amPm = if (calendar.get(Calendar.AM_PM) == Calendar.AM) "AM" else "PM"
            String.format("%d:%02d %s", if (hour == 0) 12 else hour, minute, amPm)
        }.toSet()

        return allSlots.filter { it !in bookedTimes }
    }

    /**
     * Get statistics for each stylist
     */
    fun getStylistStats(): List<StylistStats> {
        Thread.sleep(300)

        val stylistGroups = appointments.groupBy { it.stylistName }
        return stylistGroups.map { (stylistName, appointments) ->
            val totalRevenue = appointments.sumOf { it.serviceType.price }
            val services = appointments.map { it.serviceType }.distinct()

            StylistStats(
                stylistName = stylistName,
                appointmentCount = appointments.size,
                totalRevenue = totalRevenue,
                averageRating = Random.nextFloat() * 2 + 3.0f, // 3.0 - 5.0 rating
                specialties = services
            )
        }
    }

    /**
     * Get statistics for each service type
     */
    fun getServiceStats(): List<ServiceStats> {
        Thread.sleep(250)

        val serviceGroups = appointments.groupBy { it.serviceType }
        return serviceGroups.map { (serviceType, appointments) ->
            ServiceStats(
                serviceType = serviceType,
                bookingCount = appointments.size,
                totalRevenue = appointments.size * serviceType.price,
                averageDuration = serviceType.duration,
                popularityRank = serviceGroups.keys.indexOf(serviceType) + 1
            )
        }.sortedByDescending { it.bookingCount }
    }

    /**
     * Get daily revenue breakdown
     */
    fun getDailyRevenue(): DailyRevenue {
        Thread.sleep(200)

        val totalRevenue = appointments.sumOf { it.serviceType.price }
        val revenueByService = appointments.groupBy { it.serviceType }
            .mapValues { (_, appointments) -> appointments.sumOf { it.serviceType.price } }
        val revenueByStylist = appointments.groupBy { it.stylistName }
            .mapValues { (_, appointments) -> appointments.sumOf { it.serviceType.price } }

        return DailyRevenue(
            date = "Today",
            totalRevenue = totalRevenue,
            revenueByService = revenueByService,
            revenueByStylist = revenueByStylist,
            appointmentCount = appointments.size
        )
    }

    /**
     * Get client appointment history
     */
    fun getClientHistory(clientPhone: String): List<Appointment> {
        Thread.sleep(300)

        // For demo purposes, return past appointments for the client
        val clientAppointments = appointments.filter { it.clientPhone == clientPhone }

        // Create some historical appointments
        val historicalAppointments = clientAppointments.flatMap { appointment ->
            val ol = listOf(
                appointment.copy(
                    id = appointment.id + 100,
                    appointmentTime = createAppointmentTime(-30, 10, 0), // 30 days ago
                    status = AppointmentStatus.COMPLETED
                ),
                appointment.copy(
                    id = appointment.id + 200,
                    appointmentTime = createAppointmentTime(-60, 14, 30), // 60 days ago
                    status = AppointmentStatus.COMPLETED
                )
            )
            val rl = mutableListOf(
                appointment.copy(
                    id = appointment.id + 100,
                    appointmentTime = createAppointmentTime(-30, 10, 0), // 30 days ago
                    status = AppointmentStatus.COMPLETED
                )
            )
            repeat(10) {
                rl.addAll(ol)
            }

            rl
        }

        return historicalAppointments.sortedByDescending { it.appointmentTime }
    }

    /**
     * Update appointment status
     */
    fun updateAppointmentStatus(appointmentId: Int, newStatus: AppointmentStatus): Appointment? {
        Thread.sleep(200)

        val index = appointments.indexOfFirst { it.id == appointmentId }
        if (index != -1) {
            val updatedAppointment = appointments[index].copy(status = newStatus)
            // In a real app, you would update the actual data source (e.g., database)
            // For this mock, we'll simulate the update by replacing the item in the list
            // Note: This is a simplified in-memory update and won't persist across app runs
            (appointments as MutableList)[index] = updatedAppointment
            return updatedAppointment
        }
        return null
    }

    /**
     * Book a new appointment slot
     */
    fun bookAppointmentSlot(
        timeSlot: String,
        clientName: String,
        clientPhone: String,
        stylistName: String,
        serviceType: ServiceType
    ): Appointment? {
        Thread.sleep(400)

        // Parse the time slot
        val timeParts = timeSlot.split(" ")
        val time = timeParts[0].split(":")
        var hour = time[0].toInt()
        val minute = time[1].toInt()
        if (timeParts[1] == "PM" && hour != 12) hour += 12
        if (timeParts[1] == "AM" && hour == 12) hour = 0

        val newId = appointments.maxOf { it.id } + 1

        return Appointment(
            id = newId,
            clientName = clientName,
            clientPhone = clientPhone,
            stylistName = stylistName,
            serviceType = serviceType,
            appointmentTime = createAppointmentTime(hour, minute),
            status = AppointmentStatus.CONFIRMED,
            notes = "New booking via app"
        )
    }

    /**
     * Get all stylists
     */
    fun getStylists(): List<Stylist> {
        Thread.sleep(250)

        return listOf(
            Stylist(
                id = 1,
                name = "Sarah Johnson",
                specialties = listOf(ServiceType.CUT_AND_STYLE, ServiceType.BALAYAGE, ServiceType.HIGHLIGHTS),
                yearsExperience = 8,
                rating = 4.8f,
                bio = "Specializes in modern cuts and natural-looking color"
            ),
            Stylist(
                id = 2,
                name = "Emma Wilson",
                specialties = listOf(ServiceType.TRIM, ServiceType.KERATIN_TREATMENT, ServiceType.DEEP_CONDITIONING),
                yearsExperience = 5,
                rating = 4.6f,
                bio = "Expert in hair health and restoration treatments"
            ),
            Stylist(
                id = 3,
                name = "Marcus Lee",
                specialties = listOf(ServiceType.CUT_AND_STYLE, ServiceType.COLOR_TREATMENT, ServiceType.DEEP_CONDITIONING),
                yearsExperience = 10,
                rating = 4.9f,
                bio = "Master colorist with expertise in men's styling"
            ),
            Stylist(
                id = 4,
                name = "Jessica Brown",
                specialties = listOf(ServiceType.HIGHLIGHTS, ServiceType.BALAYAGE, ServiceType.COLOR_TREATMENT),
                yearsExperience = 6,
                rating = 4.7f,
                bio = "Creative color specialist, loves bold transformations"
            )
        )
    }

    /**
     * Get all available service types
     */
    fun getServiceTypes(): List<ServiceType> {
        Thread.sleep(150)
        return ServiceType.values().toList()
    }

    /**
     * Helper function to create appointment times for today
     */
    private fun createAppointmentTime(hour: Int, minute: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }

    /**
     * Helper function to create appointment times for past dates
     */
    private fun createAppointmentTime(daysAgo: Int, hour: Int, minute: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, daysAgo)
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }
}