package iuh.fit.edu.persistenceworker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Persistence Worker - Combined Background Service
 * Handles both:
 *   - WRITE: Listens to 'mq' queue → persists orders/inventory to MariaDB
 *   - READ:  Bootstraps Redis on startup + listens to 'mq-read' for cache miss recovery
 */
@SpringBootApplication
public class PersistenceWorkerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PersistenceWorkerApplication.class, args);
    }
}
