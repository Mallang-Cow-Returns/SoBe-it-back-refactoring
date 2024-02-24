package com.finalproject.mvc.sobeit.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import com.querydsl.jpa.impl.JPAQueryFactory;

@Configuration // 환경설정을 돕는 클래스
public class AppConfig {
	
	@PersistenceContext // EntityManager는 각 서비스마다 새롭게 생성해서 주입
	private EntityManager em;

	private final Map<String, String> smsMap = new HashMap<>();
	
	@Bean // 생성
	public JPAQueryFactory queryFactory() {
		return new JPAQueryFactory(em);
	}

	@Bean
	public Map<String, String> smsAuthMap() { return smsMap; }
}






