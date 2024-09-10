package br.edu.ifpb.file_service.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value(value = "${broker.queue.post.file}")
    private String postFileQueue;

    @Value(value = "${broker.queue.file.post}")
    private String filePostQueue;

    @Bean
    public Queue postFileQueue() {
        return new Queue(postFileQueue, true);
    }

    @Bean
    public Queue filePostQueue() {
        return new Queue(filePostQueue, true);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public SimpleRabbitListenerContainerFactory myRabbitListenerContainerFactory(
            SimpleRabbitListenerContainerFactoryConfigurer configurer,
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setConcurrentConsumers(8);
        factory.setMaxConcurrentConsumers(16);

        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setBatchSize(65000);

        factory.setPrefetchCount(50);
        return factory;
    }
}
