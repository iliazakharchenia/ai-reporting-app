package com.ai.reporting.service;

import com.ai.reporting.core.ReportData;
import com.ai.reporting.core.ReportResponse;
import com.ai.reporting.core.multiple.MultipleDatasourceReportData;
import com.ai.reporting.core.multiple.Report;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportingService {
    private final ChatClient client;
    public final long MILLIS_TO_SLEEP;

    public Mono<ReportResponse> reportSingle(ReportData reportData) {
        return getContentMono(reportData)
                .map(content -> ReportResponse.fromContent(reportData, content, LocalDateTime.now()))
                .subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Requests from multiple threads. Works only on a paid accounts :(((
     * <p>
     * Typical error message: '429 - {"message":"Requests rate limit exceeded"}'
     *
     * @exception org.springframework.ai.retry.NonTransientAiException
     * When requests rate is too big from your app.
     */
    public Mono<ReportResponse> reportMultiple(MultipleDatasourceReportData reportData) {
        return Flux.merge(
                reportData.dataList().parallelStream()
                .map(dataString -> new ReportData(reportData.objective(), dataString))
                .map(rd -> getShorterContentMono(rd)
                        .subscribeOn(Schedulers.boundedElastic())).collect(Collectors.toList())
        ).collectList().map(
                responseDataList -> Report.prepare(reportData.objective(), responseDataList))
                .flatMap(report -> getContentMono(
                        new ReportData(report.getObjective(), report.prepareResponsePrompt().toString()))
                        .subscribeOn(Schedulers.boundedElastic()))
                .map(contentString -> ReportResponse.fromContent(
                        reportData.objective(), contentString, LocalDateTime.now()));
    }

    public Mono<ReportResponse> reportMultipleQueue(MultipleDatasourceReportData reportData) {
        return Mono.just(reportData.dataList())
                .map(inputStrings -> {
                    List<String> accumulator = new ArrayList<>();
                    inputStrings.forEach(inputString -> {
                        accumulator.add(getShorterContent(new ReportData(reportData.objective(), inputString)));
                        try {
                            Thread.sleep(MILLIS_TO_SLEEP);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    return accumulator;
                }).subscribeOn(Schedulers.boundedElastic())
                .map(responseDataList -> Report.prepare(reportData.objective(), responseDataList))
                .flatMap(report -> getContentMono(
                        new ReportData(report.getObjective(), report.prepareResponsePrompt().toString()))
                        .subscribeOn(Schedulers.boundedElastic()))
                .map(contentString -> ReportResponse.fromContent(
                        reportData.objective(), contentString, LocalDateTime.now()));
    }

    public ReportingService(ChatClient.Builder chatClientBuilder,
                            @Value("${app.threads.millis_to_sleep}") String millisToSleepString) {
        this.client = chatClientBuilder.build();
        MILLIS_TO_SLEEP = Integer.parseInt(millisToSleepString);
    }

    private Mono<String> getContentMono(ReportData reportData) {
        return Mono.fromCallable(() -> getContent(reportData));
    }

    private Mono<String> getShorterContentMono(ReportData reportData) {
        return Mono.fromCallable(() -> getShorterContent(reportData));
    }

    private String getContent(ReportData reportData) {
        return client.prompt(reportPrompt(reportData).toString()).call().content();
    }

    private String getShorterContent(ReportData reportData) {
        return client.prompt(shorterPrompt(reportPrompt(reportData)).toString()).call().content();
    }

    private static StringBuilder reportPrompt(ReportData reportData) {
        return new StringBuilder("Make a report with respect of '")
                .append(reportData.objective())
                .append("' objective, according to the material '")
                .append(reportData.data())
                .append("'.");
    }

    private static StringBuilder shorterPrompt(StringBuilder promptBuilder) {
        return promptBuilder.append(" Make response as shorter as possible.");
    }
}
