package com.example.booking_api.entity;

public enum AppointmentStatus {
    SCHEDULED,   // Заплановано
    CONFIRMED,   // Підтверджено
    COMPLETED,   // Завершено
    CANCELLED,   // Скасовано
    NO_SHOW      // Пацієнт не з'явився
}
