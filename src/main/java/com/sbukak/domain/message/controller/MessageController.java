package com.sbukak.domain.message.controller;

import com.sbukak.domain.message.dto.MessageRequestDTO;
import com.sbukak.domain.message.dto.MessageResponseDTO;
import com.sbukak.domain.message.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
public class MessageController {

    private final MessageService messageService;

    @MessageMapping("/send/message")
    public void sendTeamMessage(MessageRequestDTO requestDTO) {
        messageService.sendMessage(requestDTO, "cheer-" +
                "messages-" + requestDTO.teamId());
    }

    @Operation(summary = "지난 메시지 팀별 조회", description = "Fetch all messages associated with a specific team ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Messages retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Team not found", content = @Content)
    })

    @GetMapping("/message/{teamId}")
    @ResponseBody
    public List<MessageResponseDTO> getMessagesByTeam(@PathVariable Long teamId) {
        return messageService.getMessagesByTeamId(teamId);
    }
}