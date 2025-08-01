package com.shop.entity;

import com.shop.constant.Role;
import com.shop.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Commit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Log4j2
class MemberTest {
    @Autowired
    MemberRepository memberRepository;


    @Autowired
    private PasswordEncoder passwordEncoder;

    @PersistenceContext
    EntityManager em;


    @Test
    @DisplayName("Auditing 테스트")
    @Commit
    public void auditingTest() {
        // 멤버 객체 생성 시 권한과 이메일 설정
        Member newMember = new Member();
        newMember.setName("공룡");
        newMember.setAddress("강원도");
        newMember.setEmail("dino@email.com");
        newMember.setPassword(passwordEncoder.encode("1234"));
        newMember.setRole(Role.ADMIN);



        memberRepository.save(newMember);
        em.flush();
        em.clear();

        Member member = memberRepository.findById(newMember.getId()).orElseThrow(EntityNotFoundException::new);
        log.info("----------------------------------------");

        log.info("resister time :" + member.getRegTime());
        log.info("update time :" + member.getUpdateTime());
        log.info("create member :" + member.getCreatedBy());
        log.info("modify member :" + member.getModifiedBy());
        log.info("----------------------------------------");
    }
}