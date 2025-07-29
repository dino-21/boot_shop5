package com.shop.config;


import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@Log4j2
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("------    securityFilterChain   ------");


        //csrf 비활성화, 람다식으로 작성해야 동작
        http.csrf(csrf -> csrf.disable());


        http
                // 로그인 설정
                .formLogin(form -> form
                        .loginPage("/members/login")       // 로그인 페이지 URL을 설정한다.
                        //.defaultSuccessUrl("/members/")
                        .defaultSuccessUrl("/members/", true)      // 로그인 성공 시 이동할 URL을 설정한다
                        .usernameParameter("email")        // 로그인 시 사용할 파라미터 이름으로 email을 지정한다.
                        .failureUrl("/members/login/error")   // 로그인 실패 시 이동할 URL을 설정한다.
                )

                // 로그아웃 설정
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/members/logout"))  // 로그아웃 URL 설정
                        .logoutSuccessUrl("/members/")  // 로그아웃 성공 시 이동할 URL을 설정한다.
                );



        // URL 권한 설정
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/js/**", "/img/**").permitAll()   // 정적 리소스는 인증 없이 접근 허용
                .requestMatchers("/", "/members/**", "/item/**", "/images/**").permitAll() // 공개 URL들
                .requestMatchers("/admin/**").hasRole("ADMIN")     // /admin 경로는 ADMIN 권한 필요
                .anyRequest().authenticated()           // 그 외 모든 요청은 인증 필요
        );

        return  http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
