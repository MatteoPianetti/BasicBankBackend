package com.example.demo.dto.acount;

import com.example.demo.model.account.Severity;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlockRequest {
    @NotBlank(message = "Il messaggio di motivazione è obbligatorio")
    private String reason;
    private String blockedBy;
    private Severity severity;
}
