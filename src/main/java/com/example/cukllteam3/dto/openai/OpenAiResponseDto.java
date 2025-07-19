package com.example.cukllteam3.dto.openai;

import lombok.Data;
import java.util.List;

@Data
public class OpenAiResponseDto {
    private List<Choice> choices;

    @Data
    public static class Choice {
        private Message message;
    }

    @Data
    public static class Message {
        private String role;
        private String content;
    }
}
