package dev.first.myspringai.parser;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.parser.BeanOutputParser;
import org.springframework.ai.parser.ListOutputParser;
import org.springframework.ai.parser.MapOutputParser;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class ParserController {

    private final ChatClient chatClient;

    public ParserController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @GetMapping("/api/author")
    public String getBooksByCraig() {
        String promptMessage = """
                Generate a list of books written by the author {author}.
                """;

        PromptTemplate promptTemplate = new PromptTemplate(promptMessage, Map.of("author","Craig Walls"));
        Prompt prompt = promptTemplate.create();
        return chatClient.prompt(prompt).call().content();
    }

    @GetMapping("/api/songslist")
    public List<String> getSongsListByArtist(@RequestParam(value = "artist", defaultValue = "BTS") String artist) {
//                아티스트 {artist}의 상위 10곡 목록을 알려주세요. 답을 모르면 "모르겠어요"라고 말하면 됩니다.
        var message = """
                Please list the top 10 songs by artist {artist}. If you don't know the answer, just say "I don't know."
                {format}
                """;
        ListOutputParser outputParser = new ListOutputParser(new DefaultConversionService());
        System.out.println("SongController format = " + outputParser.getFormat());
        PromptTemplate promptTemplate =
                new PromptTemplate(message, Map.of("artist", artist,"format", outputParser.getFormat()));
        Prompt prompt = promptTemplate.create();
        ChatResponse response = chatClient.prompt(prompt).call().chatResponse();
        return outputParser.parse(response.getResult().getOutput().getContent());
    }

    @GetMapping("/api/authorbean")
    public Author getBooksByAuthor(@RequestParam(value = "author", defaultValue = "Ken Kousen") String author) {
        String promptMessage = """
                Generate a list of books written by the author {author}. If you aren't positive that a book
                belongs to this author please don't include it.
                {format}
                """;

        var outputParser = new BeanOutputParser<>(Author.class);
        String format = outputParser.getFormat();
        System.out.println("format = " + format);

        PromptTemplate promptTemplate = new PromptTemplate(promptMessage, Map.of("author",author,"format", format));
        Prompt prompt = promptTemplate.create();
        Generation generation = chatClient.prompt(prompt).call().chatResponse().getResult();
        return outputParser.parse(generation.getOutput().getContent());
    }

    @GetMapping("/api/authormap")
    public Map<String, Object> byTopic(@RequestParam(value = "author", defaultValue = "Craig Walls") String author) {
        MapOutputParser outputParser = new MapOutputParser();
        String format = outputParser.getFormat();

        String promptMessage = """
                Generate a list of links for the author {author}. 
                Include the authors name as the key and any social network links as the object.
                {format}
                """;

        PromptTemplate promptTemplate = new PromptTemplate(promptMessage, Map.of("author",author,"format",format));
        Prompt prompt = promptTemplate.create();
        Generation generation = chatClient.prompt(prompt).call().chatResponse().getResult();
        return outputParser.parse(generation.getOutput().getContent());
    }

    // Spring AI 1.0.0 M1 released 버전
    @GetMapping("/api/authorbean-m1")
    public Author getBooksByAuthorM1(@RequestParam(value = "authorName", defaultValue = "Ken Kousen") String authorName) {
        String promptMessage = """
                Generate a list of books written by the author {author}. If you aren't positive that a book
                belongs to this author please don't include it.
                """;

        Author author = chatClient.prompt()
                .user(u -> u.text(promptMessage).param("author", authorName))
                .call()
                .entity(Author.class);
        return author;
    }

    // Spring AI 1.0.0 M1 released 버전
    @GetMapping("/api/authorlist-m1")
    public List<Author> getBooksByAuthorM1List(@RequestParam(value = "authorName", defaultValue = "조정래")
                                               String authorName) {
        String promptMessage = """
                작가 {author}가 쓴 책 목록을 알려주세요. 
                가장 최근에 쓰여진 책이 가장 먼저 출력 되도록 해주세요.
                첫번째 책의 내용을 synopsis 항목에 저장해서 출력해 주세요.
                """;
        return chatClient.prompt()
                .user(u -> u.text(promptMessage).param("author", authorName))
                .call()
                .entity(new ParameterizedTypeReference<List<Author>>() {
                });
    }
       
}