package com.vantu.springai.tools;

import com.vantu.springai.entity.HelpDeskTicket;
import com.vantu.springai.model.TicketRequest;
import com.vantu.springai.service.HelpDeskTicketService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HelpDeskTools {
    private static final Logger logger = LoggerFactory.getLogger(HelpDeskTools.class);
    private final HelpDeskTicketService helpDeskTicketService;

    @Tool(name = "createTicket", description = "Create the support ticket")
    String createTicket(@ToolParam(description = "Details to create a support ticket") TicketRequest ticketRequest, ToolContext toolContext) {
        String username = (String) toolContext.getContext().get("username");

        logger.info("Creating support ticket for user: {} with details: {}", username, ticketRequest);

        HelpDeskTicket savedTicket = helpDeskTicketService.createTicket(ticketRequest, username);

        return "Ticket #" + savedTicket.getId() + " created successfully for user " + savedTicket.getUsername();
    }

    @Tool(name = "getTickStatus", description = "Fetch the status on the tickets based a given username")
    List<HelpDeskTicket> getTickStatus(ToolContext toolContext) {
        String username = (String) toolContext.getContext().get("username");
        logger.info("Fetching tickets for user: {}", username);

        List<HelpDeskTicket> helpDeskTickets = helpDeskTicketService.getTicketsByUsername(username);
        logger.info("Found {} tickets for user: {}", helpDeskTickets.size(), username);

        return helpDeskTickets;
    }
}
