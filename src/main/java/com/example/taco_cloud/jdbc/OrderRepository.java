package com.example.taco_cloud.jdbc;

import com.example.taco_cloud.data.TacoOrder;
import com.example.taco_cloud.data.User;
import org.springframework.data.repository.CrudRepository;

import java.awt.print.Pageable;
import java.util.List;

public interface OrderRepository  extends CrudRepository<TacoOrder, Long> {

    List<TacoOrder> findByUserOrderByPlacedAtDesc(User user, Pageable pageable);


}
