package com.cuit.interviewsystem.ai.agent.model;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeApiSpec;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.netty.resources.ConnectionProvider;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;





/**
 *
 * 对于 Stream，将 WebClient 的底层引擎替换为 NettyHttpClient，并优化 Netty 资源池和连接复用
 * 对于 Call，将 RestClient 替换为 OkHttpClient/JDK HttpClient。合理设置 ConnectionPool 和 ReadTimeOut
 */
@Component
public class ChatModel {
    @Value("${spring.ai.dashscope.api-key}")
    private String API_KEY;


    public DashScopeApi dashScopeApi() {
        return DashScopeApi.builder()
                .apiKey(API_KEY)
                .build();
    }

    @Bean
    public ChatClient dashScopeChatClient() {
        return ChatClient.builder(getDashScopeChatModel())
                .defaultAdvisors(new SimpleLoggerAdvisor()).build();
    }

    private DashScopeChatModel getDashScopeChatModel() {

        return DashScopeChatModel.builder()
                .dashScopeApi(getDashscopeAPI()).defaultOptions(
                        DashScopeChatOptions.builder()
                                .model("qwen-plus")
                                .enableSearch(true)
                                .searchOptions(DashScopeApiSpec.SearchOptions.builder()
                                        .enableSource(true)
                                        .forcedSearch(true)
                                        .searchStrategy("turbo")
                                        .build()
                                ).build()
                ).build();
    }

    private DashScopeApi getDashscopeAPI() {
        // 配置HTTP连接池
        ConnectionProvider provider = ConnectionProvider.builder("dashscope")
                .maxConnections(500)
                .maxIdleTime(Duration.ofMinutes(10))  // 空闲连接保持10分钟
                .maxLifeTime(Duration.ofMinutes(30))  // 连接最大生命周期30分钟
                .evictInBackground(Duration.ofSeconds(60))  // 每60秒清理一次过期连接
                .build();

        // 配置HTTP客户端
        HttpClient httpClient = HttpClient.create(provider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)  // 连接超时10秒
                .responseTimeout(Duration.ofSeconds(60))  // 响应超时60秒
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(60))  // 读超时60秒
                        .addHandlerLast(new WriteTimeoutHandler(10))  // 写超时10秒
                );

        // 构建WebClient实例
        WebClient.Builder webClientbuilder = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient));
        // 可选配置
        // 添加请求日志记录功能
        //.filter(ExchangeFilterFunction.ofRequestProcessor(
        //        clientRequest -> {
        //            log.debug("Request: {} {}",
        //                    clientRequest.method(),
        //                    clientRequest.url());
        //            return Mono.just(clientRequest);
        //        }
        //))
        // 添加响应日志记录功能
        //.filter(ExchangeFilterFunction.ofResponseProcessor(
        //        clientResponse -> {
        //            log.debug("Response status: {}",
        //                    clientResponse.statusCode());
        //            return Mono.just(clientResponse);
        //        }
        //));

        return DashScopeApi.builder()
                .apiKey(API_KEY)
                .webClientBuilder(webClientbuilder)
                .restClientBuilder(RestClient.builder().requestFactory(new JdkClientHttpRequestFactory()))
                .build();
    }


}