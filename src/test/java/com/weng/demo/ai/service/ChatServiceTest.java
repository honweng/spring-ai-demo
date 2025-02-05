package com.weng.demo.ai.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClient.CallResponseSpec;
import org.springframework.ai.chat.client.ChatClient.ChatClientRequestSpec;
import org.springframework.ai.chat.client.ChatClient.StreamResponseSpec;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.prompt.Prompt;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class ChatServiceTest {

    @Mock
    private ChatClient.Builder chatModelBuilder;

    @Mock
    private ChatClient chatClient;

    @Mock
    private Prompt prompt;

    // @InjectMocks
    private ChatService chatService;

    @Mock
    private ChatClientRequestSpec chatClientRequestSpec;

    @Mock
    private CallResponseSpec callResponseSpec;

    @Mock
    private StreamResponseSpec streamResponseSpec;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        when(chatModelBuilder.defaultAdvisors(any(MessageChatMemoryAdvisor.class))).thenReturn(chatModelBuilder);
        when(chatModelBuilder.build()).thenReturn(chatClient);
        when(chatClient.prompt()).thenReturn(chatClientRequestSpec);

        this.chatService = new ChatService(chatModelBuilder);
    }

    @Test
    public void testChat() {
        when(chatModelBuilder.defaultAdvisors(any(MessageChatMemoryAdvisor.class))).thenReturn(chatModelBuilder);

        String message = "Hello, world!";
        String expectedResponse = "Hi there!";

        when(chatClientRequestSpec.user(message)).thenReturn(chatClientRequestSpec);
        when(chatClientRequestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.content()).thenReturn(expectedResponse);

        String response = chatService.chat(message);

        assertEquals(expectedResponse, response);
    }

    @Test
    public void testChatWithEmptyMessage() {
        String message = "";
        String expectedResponse = "Please provide a message.";

        when(chatClientRequestSpec.user(message)).thenReturn(chatClientRequestSpec);
        when(chatClientRequestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.content()).thenReturn(expectedResponse);

        String response = chatService.chat(message);

        assertEquals(expectedResponse, response);
    }

    @Test
    public void testStreamableChat() {
        String message = "Hello, world!";
        String expectedResponse = "Hi there!";

        when(chatClientRequestSpec.user(message)).thenReturn(chatClientRequestSpec);
        when(chatClientRequestSpec.stream()).thenReturn(streamResponseSpec);
        when(streamResponseSpec.content()).thenReturn(Flux.just(expectedResponse));

        Flux<String> responseFlux = chatService.streamableChat(message);

        StepVerifier.create(responseFlux)
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    public void testStreamableChatWithEmptyMessage() {
        String message = "";
        String expectedResponse = "Please provide a message.";

        when(chatClientRequestSpec.user(message)).thenReturn(chatClientRequestSpec);
        when(chatClientRequestSpec.stream()).thenReturn(streamResponseSpec);
        when(streamResponseSpec.content()).thenReturn(Flux.just(expectedResponse));

        Flux<String> responseFlux = chatService.streamableChat(message);

        StepVerifier.create(responseFlux)
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    public void testStreamableChatMultipleResponses() {
        String message = "Hello, world!";
        String expectedResponse1 = "Hi there!";
        String expectedResponse2 = "How can I help you?";

        when(chatClientRequestSpec.user(message)).thenReturn(chatClientRequestSpec);
        when(chatClientRequestSpec.stream()).thenReturn(streamResponseSpec);
        when(streamResponseSpec.content()).thenReturn(Flux.just(expectedResponse1, expectedResponse2));
        Flux<String> responseFlux = chatService.streamableChat(message);

        StepVerifier.create(responseFlux)
                .expectNext(expectedResponse1)
                .expectNext(expectedResponse2)
                .verifyComplete();
    }

    @Test
    public void testStreamableChatNoResponse() {
        String message = "Hello, world!";

        when(chatClientRequestSpec.user(message)).thenReturn(chatClientRequestSpec);
        when(chatClientRequestSpec.stream()).thenReturn(streamResponseSpec);
        when(streamResponseSpec.content()).thenReturn(Flux.empty());

        Flux<String> responseFlux = chatService.streamableChat(message);

        StepVerifier.create(responseFlux)
                .verifyComplete();
    }
}