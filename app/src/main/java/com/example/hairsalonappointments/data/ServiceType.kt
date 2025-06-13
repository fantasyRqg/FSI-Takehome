package com.example.hairsalonappointments.data

enum class ServiceType(val displayName: String, val duration: Int, val price: Double) {
    CUT_AND_STYLE("Cut & Style", 60, 65.00),
    COLOR_TREATMENT("Color Treatment", 120, 120.00),
    HIGHLIGHTS("Highlights", 150, 150.00),
    KERATIN_TREATMENT("Keratin Treatment", 180, 250.00),
    BALAYAGE("Balayage", 180, 200.00),
    TRIM("Trim", 30, 35.00),
    BLOWOUT("Blowout", 45, 50.00),
    DEEP_CONDITIONING("Deep Conditioning", 45, 60.00),
    UPDO("Updo", 60, 80.00),
    EXTENSIONS("Extensions", 240, 400.00)
}