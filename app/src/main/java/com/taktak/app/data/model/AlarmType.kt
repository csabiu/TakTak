package com.taktak.app.data.model

/**
 * Types of alarms for brew batch management
 */
enum class AlarmType {
    NEXT_STAGE,      // Alarm for adding next stage in multi-stage brewing
    FILTER,          // Alarm for when brew needs to be filtered
    CHECK_STATUS,    // Alarm for checking fermentation status
    COMPLETION,      // Alarm for when fermentation is expected to complete
    CUSTOM           // Custom user-defined alarm
}
