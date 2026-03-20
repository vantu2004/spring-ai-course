package com.vantu.mcpserverremote.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "helpdesk_tickets")
public class HelpDeskTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String issue;

    // e.g., OPEN, IN_PROGRESS, CLOSED
    private String status;
    private LocalDateTime createdAt;

    // Estimated Time of Arrival
    private LocalDateTime eta;

}