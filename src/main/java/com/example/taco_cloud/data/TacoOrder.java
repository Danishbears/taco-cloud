package com.example.taco_cloud.data;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.CreditCardNumber;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@Entity
public class TacoOrder implements Serializable {


    public enum Status{
        CREATED,COOKING,DELIVERING,COMPLETED
    }

    @Enumerated(EnumType.STRING)
    private Status status = Status.CREATED;

    @PrePersist
    void placedAt(){
        this.placedAt = new Date();
        if(this.status == null){
            this.status = Status.CREATED;
        }
    }

    @ManyToOne
    private User user;

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Date placedAt = new Date();


    @NotBlank(message = "Delivery name is required")
    private String deliveryName;
    @NotBlank(message = "Street is required")
    private String deliveryStreet;
    @NotBlank(message = "City is required")
    private String deliveryCity;
    @NotBlank(message = "State is required")
    private String deliveryState;
    @NotBlank(message="Zip code is required")
    private String deliveryZip;
    @CreditCardNumber(message="Not a valid credit card number")
    private String ccNumber;
    @Pattern(regexp="^(0[1-9]|1[0-2])([\\/])([2-9][0-9])$",
            message="Must be formatted MM/YY")
    private String ccExpiration;
    @Digits(integer=3, fraction=0, message="Invalid CVV")
    private String ccCVV;

    @OneToMany(cascade = CascadeType.ALL)
    @Column(name = "tacos")
    private List<Taco> tacos = new ArrayList<>();

    public void addTaco(Taco taco){
        this.tacos.add(taco);
    }

    @ManyToOne
    @JoinColumn(name = "coupon_id")
    private Coupon appliedCoupon;

    private BigDecimal discountAmount = BigDecimal.ZERO;



    public BigDecimal getDiscountAmount() {
        if (appliedCoupon == null || tacos == null || tacos.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal rawTotal = tacos.stream()
                .map(taco -> BigDecimal.valueOf(taco.getPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discountPercent = appliedCoupon.getDiscountPercent();
        if (discountPercent == null) {
            return BigDecimal.ZERO;
        }

        return rawTotal.multiply(discountPercent)
                .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
    }

    public BigDecimal getTotalPrice() {
        if (tacos == null || tacos.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal rawTotal = tacos.stream()
                .map(taco -> {
                    BigDecimal price = BigDecimal.valueOf(taco.getPrice());
                    int qty = taco.getQuantity() > 0 ? taco.getQuantity() : 1;
                    return price.multiply(BigDecimal.valueOf(qty));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discount = getDiscountAmount();
        if (discount == null) {
            discount = BigDecimal.ZERO;
        }

        BigDecimal finalTotal = rawTotal.subtract(discount);
        return finalTotal.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : finalTotal;
    }
}
