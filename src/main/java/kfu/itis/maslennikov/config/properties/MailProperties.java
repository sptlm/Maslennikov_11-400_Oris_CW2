package kfu.itis.maslennikov.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix="mail")
public record MailProperties(
        String from,
        String sender,
        String subject,
        String content,
        String baseUrl
) {
}
