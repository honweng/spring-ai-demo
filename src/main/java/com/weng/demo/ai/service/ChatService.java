package com.weng.demo.ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import reactor.core.publisher.Flux;

@Service
public class ChatService {

    private final ChatClient chatModel;

    public ChatService(ChatClient.Builder chatModelBuilder) {
        this.chatModel = chatModelBuilder
                .defaultAdvisors(new MessageChatMemoryAdvisor(new InMemoryChatMemory()))// CHAT MEMORY
                .build();
    }

    public String chat(String message) {
        return this.chatModel.prompt().user(message).call().content();
    }

    public Flux<String> streamableChat(String message) {
        return this.chatModel.prompt().user(message).stream().content().map(content -> HtmlUtils.htmlEscape(content));
    }
}
