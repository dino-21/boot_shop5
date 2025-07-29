package com.shop.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {

        // 스프링 시큐리티로부터 로그인한 사용자 ID 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String userId = "";
        //현재 로그인 한 사용자의 정보를 조회하여 사용자의 이름을 등록자와 수정자로 지정한다.
        if(authentication != null) {
            userId = authentication.getName();
        }
        return Optional.of(userId);  // 사용자 이름 반환
    }
}

