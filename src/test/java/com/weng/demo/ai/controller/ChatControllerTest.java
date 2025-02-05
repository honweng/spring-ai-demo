package com.weng.demo.ai.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.weng.demo.ai.service.ChatService;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class ChatControllerTest {
    @Mock
    private ChatService chatService;

    @InjectMocks
    private ChatController chatController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testChat() {
        String message = "Hello, world!";
        String expectedResponse = "Hi there!";

        when(chatService.chat(message)).thenReturn(expectedResponse);

        String response = chatController.chat(message);

        assertEquals(expectedResponse, response);
    }

    @Test
    public void testChatWithEmptyMessage() {
        String message = "";
        String expectedResponse = "Please provide a message.";

        when(chatService.chat(message)).thenReturn(expectedResponse);

        String response = chatController.chat(message);

        assertEquals(expectedResponse, response);
    }

    @Test
    public void testChatWithStream() {
        String message = "Hello, world!";
        String expectedResponse = "Hi there!";
        when(chatService.streamableChat(any(String.class))).thenReturn(Flux.just(expectedResponse));

        Flux<String> responseFlux = chatController.chatWithStream(message);

        StepVerifier.create(responseFlux)
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    public void testChatWithStreamEmptyMessage() {
        String message = "";
        String expectedResponse = "Please provide a message.";

        when(chatService.streamableChat(any(String.class))).thenReturn(Flux.just(expectedResponse));

        Flux<String> responseFlux = chatController.chatWithStream(message);

        StepVerifier.create(responseFlux)
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    public void testChatWithStreamMultipleResponses() {
        String message = "Hello, world!";
        String expectedResponse1 = "Hi there!";
        String expectedResponse2 = "How can I help you?";
        when(chatService.streamableChat(any(String.class))).thenReturn(Flux.just(expectedResponse1, expectedResponse2));

        Flux<String> responseFlux = chatController.chatWithStream(message);

        StepVerifier.create(responseFlux)
                .expectNext(expectedResponse1)
                .expectNext(expectedResponse2)
                .verifyComplete();
    }

    @Test
    public void testChatWithStreamNoResponse() {
        String message = "Hello, world!";

        when(chatService.streamableChat(any(String.class))).thenReturn(Flux.empty());

        Flux<String> responseFlux = chatController.chatWithStream(message);

        StepVerifier.create(responseFlux)
                .verifyComplete();
    }
}