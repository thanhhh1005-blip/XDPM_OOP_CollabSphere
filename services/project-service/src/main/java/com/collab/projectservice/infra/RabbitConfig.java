package com.collab.projectservice.infra;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    // ====== NÊN gom constant để khỏi sai chữ ======
    public static final String PROJECT_EXCHANGE = "project.exchange";

    // Event: project submitted
    public static final String PROJECT_SUBMITTED_QUEUE = "project.submitted.queue";
    public static final String PROJECT_SUBMITTED_KEY   = "project.submitted";

    // (Tuỳ chọn) Event: project approved
    public static final String PROJECT_APPROVED_QUEUE = "project.approved.queue";
    public static final String PROJECT_APPROVED_KEY   = "project.approved";

    // (Tuỳ chọn) Event: project assigned to class
    public static final String PROJECT_ASSIGNED_QUEUE = "project.assigned.queue";
    public static final String PROJECT_ASSIGNED_KEY   = "project.assigned";

    @Bean
    public TopicExchange projectExchange() {
        return new TopicExchange(PROJECT_EXCHANGE, true, false);
    }

    // ====== Queues ======
    @Bean
    public Queue projectSubmittedQueue() {
        return QueueBuilder.durable(PROJECT_SUBMITTED_QUEUE).build();
    }

    @Bean
    public Queue projectApprovedQueue() {
        return QueueBuilder.durable(PROJECT_APPROVED_QUEUE).build();
    }

    @Bean
    public Queue projectAssignedQueue() {
        return QueueBuilder.durable(PROJECT_ASSIGNED_QUEUE).build();
    }

    // ====== Bindings ======
    @Bean
    public Binding bindProjectSubmitted(TopicExchange projectExchange) {
        return BindingBuilder.bind(projectSubmittedQueue())
                .to(projectExchange)
                .with(PROJECT_SUBMITTED_KEY);
    }

    @Bean
    public Binding bindProjectApproved(TopicExchange projectExchange) {
        return BindingBuilder.bind(projectApprovedQueue())
                .to(projectExchange)
                .with(PROJECT_APPROVED_KEY);
    }

    @Bean
    public Binding bindProjectAssigned(TopicExchange projectExchange) {
        return BindingBuilder.bind(projectAssignedQueue())
                .to(projectExchange)
                .with(PROJECT_ASSIGNED_KEY);
    }

    // ====== JSON Converter ======
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // ====== Gắn converter vào RabbitTemplate để gửi object JSON ổn định ======
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }
}
