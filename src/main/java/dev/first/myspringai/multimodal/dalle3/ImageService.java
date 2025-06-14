package dev.first.myspringai.multimodal.dalle3;

import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImageOptionsBuilder;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.stereotype.Service;

@Service
class ImageService {
    private final ImageModel imageModel;

    ImageService(ImageModel imageModel) {
        this.imageModel = imageModel;
    }

    String generate(String instructions) {
        var options = ImageOptionsBuilder.builder()
                .withHeight(1024).withWidth(1024)
                .withResponseFormat("url")
                .withModel("dall-e-3")
                .build();
        ImagePrompt imagePrompt = new ImagePrompt(instructions, options);
        ImageResponse imageResponse = imageModel.call(imagePrompt);
        return imageResponse.getResult().getOutput().getUrl();
    }
}