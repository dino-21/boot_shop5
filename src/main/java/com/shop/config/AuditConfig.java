package com.shop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")  //JPA Auditing 기능 활성화 (감시 기능 켜기)
public class AuditConfig {

    //Bean auditorProvider 누가 만들었는지 알려주는 로직을 등록
    @Bean   //등록자와 수정자를 처리해주는 AuditorAware을 빈으로 등록한다.
    public AuditorAware<String> auditorProvider() {
        return new AuditorAwareImpl();   // AuditorAwareImpl 클래스와 연결됨
    }
}
