package com.weng.demo.ai.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class ChatControllerTest {
    @Mock
    private OllamaChatModel chatModel;

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

        when(chatModel.call(message)).thenReturn(expectedResponse);

        String response = chatController.chat(message);

        assertEquals(expectedResponse, response);
    }

    @Test
    public void testChatWithEmptyMessage() {
        String message = "";
        String expectedResponse = "Please provide a message.";

        when(chatModel.call(message)).thenReturn(expectedResponse);

        String response = chatController.chat(message);

        assertEquals(expectedResponse, response);
    }

    @Test
    public void testChatWithStream() {
        String message = "Hello, world!";
        String expectedResponse = "Hi there!";

        ChatResponse chatResponse = new ChatResponse(Collections.singletonList(new Generation(new AssistantMessage(
                expectedResponse))));

        when(chatModel.stream(any(Prompt.class))).thenReturn(Flux.just(chatResponse));

        Flux<String> responseFlux = chatController.chatWithStream(message);

        StepVerifier.create(responseFlux)
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    public void testChatWithStreamEmptyMessage() {
        String message = "";
        String expectedResponse = "Please provide a message.";

        ChatResponse chatResponse = new ChatResponse(Collections.singletonList(new Generation(new AssistantMessage(
                expectedResponse))));
        when(chatModel.stream(any(Prompt.class))).thenReturn(Flux.just(chatResponse));

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

        ChatResponse chatResponse1 = new ChatResponse(Collections.singletonList(new Generation(new AssistantMessage(
                expectedResponse1))));
        ChatResponse chatResponse2 = new ChatResponse(Collections.singletonList(new Generation(new AssistantMessage(
                expectedResponse2))));
        when(chatModel.stream(any(Prompt.class))).thenReturn(Flux.just(chatResponse1, chatResponse2));

        Flux<String> responseFlux = chatController.chatWithStream(message);

        StepVerifier.create(responseFlux)
                .expectNext(expectedResponse1)
                .expectNext(expectedResponse2)
                .verifyComplete();
    }

    @Test
    public void testChatWithStreamNoResponse() {
        String message = "Hello, world!";

        Prompt prompt = new Prompt(new UserMessage(message));

        when(chatModel.stream(any(Prompt.class))).thenReturn(Flux.empty());

        Flux<String> responseFlux = chatController.chatWithStream(message);

        StepVerifier.create(responseFlux)
                .verifyComplete();
    }
}