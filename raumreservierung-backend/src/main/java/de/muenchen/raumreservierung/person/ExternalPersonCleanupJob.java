package de.muenchen.raumreservierung.person;

import de.muenchen.raumreservierung.configuration.SchedulingProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExternalPersonCleanupJob {

    private final PersonService personService;

    private final SchedulingProperties schedulingProperties;

    /* package */
    @Scheduled(cron = "${raumreservierung.scheduling.ext-person-cleanup}")
    void cleanupExternalPerson() {
        final int monthsCutOff = schedulingProperties.getMaxExtPersonAgeMonths();

        final int amountOfDeletedExternalPersons = personService.deleteExternalPersonsOlderThan(monthsCutOff);
        log.info("Scheduled-Job: removed {} external persons older than {} months", amountOfDeletedExternalPersons, monthsCutOff);
    }

}
