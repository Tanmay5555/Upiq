package com.upiq.research.dataset.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * EXPERIMENTAL: classpath definition of a synthetic research profile.
 * Not a production user DTO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResearchProfileDefinition {

    private String id;
    private String email;
    private String username;
    @JsonProperty("full_name")
    private String fullName;
    private String city;
    private String occupation;
    private String notes;
}
