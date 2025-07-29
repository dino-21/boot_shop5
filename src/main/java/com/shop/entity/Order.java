package com.shop.entity;

import com.shop.constant.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// 주문 엔티티
@Entity
@Table(name="orders")    // 테이블명을 orders로 지정
@Getter
@Setter
public class Order extends BaseEntity{
    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private  Long id;


    // 주문을 한 회원 (다대일 관계: 여러 주문이 한 회원에 매핑됨)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;   // 외래 키(FK): member_id

    private LocalDateTime orderDate; //주문일

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;  //주문상태 (예: ORDER, CANCEL)

    // 주문상품들과의 관계 (일대다)
    // 부모 엔티티의 영속성 상태 변화를 자식 엔티티에 모두 전이하는 CascadeTypeAll 옵션을 설정한다.
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();  //OrderItem 주인

    //private LocalDateTime regTime;

    //private LocalDateTime updateTime;
}
