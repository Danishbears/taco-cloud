package com.example.taco_cloud.repositories;

import com.example.taco_cloud.data.TacoOrder;
import com.example.taco_cloud.data.User;
import org.springframework.data.repository.CrudRepository;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface OrderRepository  extends CrudRepository<TacoOrder, Long> {

    List<TacoOrder> findByUserOrderByPlacedAtDesc(User user, Pageable pageable);

    List<TacoOrder> findByStatusNot(TacoOrder.Status status);
}
