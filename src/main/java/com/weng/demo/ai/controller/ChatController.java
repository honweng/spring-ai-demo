package com.weng.demo.ai.controller;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;

@RestController
@CrossOrigin
public class ChatController {

    private final OllamaChatModel chatModel;

    public ChatController(OllamaChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @PostMapping("/chat")
    public String chat(@RequestParam String message) {
        return chatModel.call(message);
    }

    @GetMapping("/stream")
    public Flux<String> chatWithStream(@RequestParam String message) {

        Prompt prompt = new Prompt(new UserMessage(message));
        return this.chatModel.stream(prompt).map(chatResponse -> chatResponse.getResult().getOutput().getContent());
    }

}