package dev.first.myspringai.multimodal;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Media;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
public class ImageModal {

    private final ChatClient chatClient;

    @Value("classpath:/images/sincerely-media.jpg")
    private Resource jpgImage;

    @Value("classpath:/images/java-open-ai.png")
    private Resource pngImage;

    public ImageModal(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @GetMapping("/image-describe")
    public String describeImage() throws IOException {
//        byte[] imageData = new ClassPathResource("/images/sincerely-media.jpg").getContentAsByteArray();
        UserMessage userMessage =
                new UserMessage("Can you please explain what you see in the following image?",
//                new UserMessage("다음 이미지에 보여지는 내용을 설명해 주시겠습니까?",
                        List.of(new Media(MimeTypeUtils.IMAGE_JPEG, jpgImage)));
        var response = chatClient
                .prompt(new Prompt(userMessage))
                .call()
                .content();
        return response;
    }

    @GetMapping("/code-describe")
    public String code() throws IOException {
//        byte[] imageData = new ClassPathResource("/images/java-open-ai.png").getContentAsByteArray();

        UserMessage userMessage =
                new UserMessage("The following is a screenshot of some code. Can you do your best to provide a description of what this code does?",
                List.of(new Media(MimeTypeUtils.IMAGE_PNG, pngImage)));
        var response = chatClient
                .prompt(new Prompt(userMessage))
                .call()
                .content();
        return response;
    }

    @GetMapping("/image-to-code")
    public String imageToCode() throws IOException {
        UserMessage userMessage =
                new UserMessage("The following is a screenshot of some code. Can you translate this from the image into text?",
                List.of(new Media(MimeTypeUtils.IMAGE_PNG, pngImage)));
        var response = chatClient
                .prompt(new Prompt(userMessage))
                .call()
                .content();
        return response;
    }

}