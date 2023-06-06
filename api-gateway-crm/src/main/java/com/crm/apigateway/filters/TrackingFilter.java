package com.crm.apigateway.filters;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@Order(1)
@Component
@Log4j2
public class TrackingFilter implements GlobalFilter {

		
	@Autowired
	FilterUtils filterUtils;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		
		log.info("Pre Filter executed");
		
		
		
		HttpHeaders requestHeaders = exchange.getRequest().getHeaders();
		
		if (isCorrelationIdPresent(requestHeaders)) {
			log.info(
					"tmx-correlation-id found in tracking filter: {}. ",
					filterUtils.getCorrelationId(requestHeaders));
		} else {
			String correlationId = generateCorrelationId();
			exchange =filterUtils.setCorrelationId(exchange, correlationId);
			log.info(
					"tmx-correlation-id generated in tracking filter: {}.",
					correlationId);
		}
		
		log.info(requestHeaders);
		
		return chain.filter(exchange);
	}

	public boolean isCorrelationIdPresent(HttpHeaders requestHeaders) {
		if (filterUtils.getCorrelationId(requestHeaders) != null) {
			return true;
		} else {
			return false;
		}
	}

	private String generateCorrelationId() {
		return java.util.UUID.randomUUID().toString();
	}

}
