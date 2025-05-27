package dev.first.myspringai.multimodal.dalle3;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
class ImageController {
    private final ImageService imageService;

    ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping("/api/image")
    Map<String, String> generateImage(@RequestParam(value = "message",
            defaultValue = "Create a movie poster for a horror film titled 'The man next door.’") String instructions) {
//        instructions = """
//                The image depicts a cozy scene, likely in a bedroom. Here's what I observe:
//
//                1. **Laptop**: There is a silver laptop with a recognizable logo on it, indicating that it is an Apple MacBook, placed on a bed.
//
//                2. **Coffee Mug**: In front of the laptop, there is a large gray mug filled with coffee or a latte. The coffee appears to have some latte art on top.
//
//                3. **Glasses**: Next to the coffee mug, there is a pair of eyeglasses with dark frames.
//
//                4. **Wooden Tray**: The coffee mug and the glasses are placed on a small wooden tray with a handle.
//                """;
        instructions = """
                이미지에는 침대 위에 놓인 은색 애플 맥북과 그 앞에 있는 작은 나무 판자가 보입니다.\s
                나무 판자 위에는 큰 회색 머그잔에 담긴 라떼와 안경이 놓여 있습니다. 머그잔의 라떼는 아름다운 라떼 아트로 장식되어 있습니다.\s
                침대는 흰색 침구로 덮여 있으며, 전체적으로 깔끔하고 편안한 분위기를 자아냅니다.
                """;
        var url = imageService.generate(instructions);
        return Map.of("url", url);
    }
}