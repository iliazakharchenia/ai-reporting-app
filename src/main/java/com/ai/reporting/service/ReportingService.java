package com.ai.reporting.service;

import com.ai.reporting.core.ReportData;
import com.ai.reporting.core.ReportResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;

@Service
public class ReportingService {
    private final ChatClient client;

    public Mono<ReportResponse> reportSingle(ReportData reportData) {
        return Mono.fromCallable(() -> {
            String content = client.prompt(
                    "Make a report with respect of '"
                    + reportData.objective() + "' objective, according to the material '"
                    + reportData.data() + "'."
                    )
                    .call().content();

            ReportResponse response = ReportResponse.fromContent(reportData, content, LocalDateTime.now());

            return response;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public ReportingService(ChatClient.Builder chatClientBuilder) {
        this.client = chatClientBuilder.build();
    }
}
