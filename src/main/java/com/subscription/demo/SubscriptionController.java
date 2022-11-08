package com.subscription.demo;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;


@Controller
public class SubscriptionController {
    @SubscriptionMapping
    public Flux<Order> orders(@Argument Long id) {
        Flux<Order> flux = Flux.concat(Flux.generate(() -> id, (orderId, synchronousSink) -> {
            List<Order> orders = getNewOrdersFromDB(orderId);
            Flux<Order> orderFlux = Flux.fromIterable(orders);
            synchronousSink.next(orderFlux);
            if(orders.isEmpty())
                return orderId;
            else
                return orders.get(orders.size()-1).getId();
        }));
        return flux;
    }
    private List<Order> getNewOrdersFromDB(Long id) {
        List<Order> orders = new ArrayList<>();
        if(id<10){
            Order order = new Order(id+1);
            orders.add(order);
            System.out.println("New Order: "+order);
        }else{
            System.out.println("No Orders Found");
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return orders;
    }
    @QueryMapping
    Order order(@Argument Long id) {
        return new Order(id);
    }
}
