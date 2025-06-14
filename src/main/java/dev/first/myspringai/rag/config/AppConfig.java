package dev.first.myspringai.rag.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.nio.charset.Charset;
import java.util.List;

@Configuration
class AppConfig {
    private static final Logger log = LoggerFactory.getLogger(AppConfig.class);

    @Value("classpath:/docs/people.txt")
    private Resource resource;

    @Bean
    VectorStore vectorStore(EmbeddingModel embeddingModel) {
        return new SimpleVectorStore(embeddingModel);
    }

    @Bean
    ApplicationRunner runner(VectorStore vectorStore) {
        return args -> {
            log.info("Loading file(s) as Documents");
            var textReader = new TextReader(resource);
            textReader.setCharset(Charset.defaultCharset());
            List<Document> documents = textReader.get();

            vectorStore.add(documents);
        };
    }
}