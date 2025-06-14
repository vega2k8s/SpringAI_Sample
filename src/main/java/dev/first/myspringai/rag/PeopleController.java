package dev.first.myspringai.rag;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.document.Document;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class PeopleController {
    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public PeopleController(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
        this.chatClient = chatClientBuilder.build();
        this.vectorStore = vectorStore;
    }

    @GetMapping("/api/rag/people")
    Person chatWithRag(@RequestParam(value="name", defaultValue = "마틴") String name) {
        List<Document> similarDocuments =
                vectorStore.similaritySearch(SearchRequest.query(name).withTopK(2));
        String information = similarDocuments
                .stream()
                .map(Document::getContent)
                .collect(Collectors.joining(System.lineSeparator()));

        var systemPromptTemplate = new SystemPromptTemplate("""
              You are a helpful assistant.
              
              Use the following information to answer the question:
              {information}
              """);
        var systemMessage = systemPromptTemplate.createMessage(
                Map.of("information", information));

        var outputConverter = new BeanOutputConverter<>(Person.class);
        PromptTemplate userMessagePromptTemplate = new PromptTemplate("""
        Tell me about {name} as if current date is {current_date}.
        {format}
        """);
        Map<String,Object> model = Map.of("name", name,
                "current_date", LocalDate.now(),
                "format", outputConverter.getFormat());
        var userMessage = new UserMessage(userMessagePromptTemplate.create(model).getContents());

        var prompt = new Prompt(List.of(systemMessage, userMessage));

        var response = chatClient.prompt(prompt).call().content();

        return outputConverter.convert(response);
    }
}

record Person(String name,
              String dateOfBirth,
              int experienceInYears,
              List<String> books) {
}
