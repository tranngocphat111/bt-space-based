package iuh.fit.edu.persistenceworker.listener;

import iuh.fit.edu.persistenceworker.service.PersistenceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Application Startup Listener
 * Triggers data bootstrap to Redis on application startup
 */
@Component
@Slf4j
public class ApplicationStartupListener {

    private final PersistenceService persistenceService;

    public ApplicationStartupListener(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("Application started, triggering data bootstrap...");
        try {
            persistenceService.bootstrapDataToRedis();
        } catch (Exception e) {
            log.error("Failed to bootstrap data on startup", e);
        }
    }
}
