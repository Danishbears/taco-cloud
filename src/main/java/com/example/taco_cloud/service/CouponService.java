package com.example.taco_cloud.service;

import com.example.taco_cloud.data.Coupon;
import com.example.taco_cloud.data.TacoOrder;
import com.example.taco_cloud.repositories.CouponRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CouponService {

    private final CouponRepository couponRepo;

    public CouponService(CouponRepository couponRepo) {
        this.couponRepo = couponRepo;
    }

    public boolean applyCouponToOrder(String code, TacoOrder order) {
        if (code == null || code.trim().isEmpty()) {
            return false;
        }

        Optional<Coupon> couponOpt = couponRepo.findByCodeIgnoreCase(code.trim());

        if (couponOpt.isPresent() && couponOpt.get().isValid()) {
            order.setAppliedCoupon(couponOpt.get());
            return true;
        }

        return false;
    }

    public void removeCouponFromOrder(TacoOrder order) {
        order.setAppliedCoupon(null);
    }
}