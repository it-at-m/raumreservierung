package de.muenchen.raumreservierung.weekschedule;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WeekScheduleCategory {
    ALTES_RATHAUS("Altes Rathaus"),
    NEUES_RATHAUS("Neues Rathaus");

    private final String label;
}
