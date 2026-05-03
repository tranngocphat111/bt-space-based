package iuh.fit.edu.persistenceworker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for cache miss recovery requests from PU2 via 'mq-read' queue.
 * PU2 sends this payload when a product is not found in Redis.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CacheMissRequest {

    @JsonProperty("product_id")
    private Integer productId;
}
