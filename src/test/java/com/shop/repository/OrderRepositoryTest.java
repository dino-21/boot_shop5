package com.shop.repository;

import com.shop.constant.ItemSellStatus;
import com.shop.entity.Item;
import com.shop.entity.Member;
import com.shop.entity.Order;
import com.shop.entity.OrderItem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderRepositoryTest {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ItemRepository itemRepository;

    @PersistenceContext
    EntityManager em;


    @Autowired
    private MemberRepository memberRepository;


    @Autowired
    OrderItemRepository orderItemRepository;


    public Item createItem() {
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("상세설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        return item;
    }

    @Test
    @DisplayName("영속성 전이 테스트")
    //@Commit //DB에 반영된걸 확인하기위해
    public void cascadeTest() {

        Order order = new Order();

        for(int i=0;i<3;i++){
            Item item = this.createItem();  // 상품 객체 3개 생성
            itemRepository.save(item);
            OrderItem orderItem = new OrderItem(); //주문상품항목 생성
            orderItem.setItem(item);  //  주문상품 항목에 어떤 상품인지 연결
            orderItem.setCount(10);
            orderItem.setOrderPrice(1000);
            orderItem.setOrder(order); //하나의 주문에 연결
            //아직 영속성 컨텍스트에 저장되지 않은 orderItem 엔티티를 order엔티티에 담아준다.
            order.getOrderItems().add(orderItem);  // 주문에 상품항목추가,
            //Order(주문) 안에 OrderItem(주문상품) 을 리스트에 추가, db에 아직 저장 안됨, 영속성만 등록
        }

        // order 엔티티를 저장하면서 강제로 flush를 호출하여
        // 영속성 컨텍스트에 있는 객체들을 데이터베이스에 반영한다.
        orderRepository.saveAndFlush(order); //주문상품(OrderItem) 3개도 자동으로 함께 DB에 저장

        //영속성 컨텍스트의 상태를 초기화한다.
        em.clear();

        //영속성 컨텍스트를 초기화했기 때문에 데이터베이스 주문 엔티티를 조회한다.
        //select 쿼리문이 실행되는 것을 콘솔창에서 확인한다.
        //주문 안에 주문상품(OrderItem) 이 정확히 3개 저장되었는지 확인 DB에서 확인
        Order savedOrder = orderRepository.findById(order.getId())
                .orElseThrow(EntityNotFoundException::new);
        assertEquals(3, savedOrder.getOrderItems().size());
    }




    //주문 데이터를 생성해서 저장하는 메서드를 만든다.
    public Order createOrder(){
        Order order = new Order();

        for(int i=0;i<3;i++){
            Item item = createItem();
            itemRepository.save(item);
            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item);
            orderItem.setCount(10);
            orderItem.setOrderPrice(1000);
            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem);
        }

        Member member = new Member();
        memberRepository.save(member);

        order.setMember(member);
        orderRepository.save(order);
        return order;
    }

    @Test
    @DisplayName("고아객체 제거 테스트")
   // @Commit //DB에 반영된걸 확인하기위해
    public void orphanRemovalTest(){
        Order order = this.createOrder();
        //order 엔티티에서 관리하고 있는 orderItem 리스트의 0번째 인덱스 요소를 제거한다.
        order.getOrderItems().remove(0);
        em.flush();
    }


    @Test
    @DisplayName("지연 로딩 테스트")
    @Commit  //DB에 반영된걸 확인하기위해
    public void lazyLoadingTest(){
        //기존에 만들었던 주문 생성 메서드를 이용하여 주문 데이터를 저장한다.
        Order order = this.createOrder();
        Long orderItemId = order.getOrderItems().get(0).getId();
        em.flush();
        em.clear();

        // 영속성 컨텍스트의 상태 초기화 후 order 엔티티에 저장했던 주문 상품 아이디를 이용하여
        // orderItem을 데이터베이스에서 조회한다.
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(EntityNotFoundException::new);
        // System.out.println("===>");
        // System.out.println("Order class:" +orderItem.getOrder().getClass());

        // orderItem 엔티티에 있는 order 객체의 클래스를 출력한다.
        System.out.println("===========================");
        System.out.println("Order class : " + orderItem.getOrder().getClass());// ①

        orderItem.getOrder().getOrderDate(); //②
        System.out.println("===========================");

    }

}

