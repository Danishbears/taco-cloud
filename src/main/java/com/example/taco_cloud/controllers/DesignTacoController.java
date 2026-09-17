package com.example.taco_cloud.controllers;

import com.example.taco_cloud.data.Ingredient;
import com.example.taco_cloud.data.Taco;
import com.example.taco_cloud.data.TacoOrder;
import com.example.taco_cloud.jdbc.IngredientRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/design")
@SessionAttributes("tacoOrder")
public class DesignTacoController {

    private final IngredientRepository ingredientRepository;

    @Autowired
    public DesignTacoController(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    @ModelAttribute
    public void addIngredientsToModel(Model model) {

        List<Ingredient> ingredients = new ArrayList<>();
        ingredientRepository.findAll().forEach(ingredients::add);


        Ingredient.Type[] types = Ingredient.Type.values();
        for (Ingredient.Type type : types) {
            model.addAttribute(type.toString().toLowerCase(),
                    filterByType(ingredients, type));
        }
    }

    private Iterable<Ingredient> filterByType(List<Ingredient> ingredients, Ingredient.Type type) {
        return ingredients
                .stream()
                .filter(x -> x.getType().equals(type))
                .collect(Collectors.toList());
    }

    @ModelAttribute(name = "tacoOrder")
    public TacoOrder order() {
        return new TacoOrder();
    }

    @ModelAttribute(name = "taco")
    public Taco taco() {
        return new Taco();
    }

    @GetMapping
    public String showDesignForm() {
        return "design";
    }

    @PostMapping("/delete")
    public String deleteTaco(@RequestParam("index") int index,
                             @ModelAttribute("tacoOrder") TacoOrder tacoOrder) {

        System.out.println("=== Trying to delete a taco ===");
        System.out.println("Index: " + index);

        if (tacoOrder != null && tacoOrder.getTacos() != null) {
            System.out.println("Amount of tacos before delete operation " + tacoOrder.getTacos().size());

            if (index >= 0 && index < tacoOrder.getTacos().size()) {
                tacoOrder.getTacos().remove(index);
                System.out.println("Taco was successfully deleted " + index);
            } else {
                System.out.println("Error: wrong index");
            }

            System.out.println("Amount of tacos after delete operation " + tacoOrder.getTacos().size());
        } else {
            System.out.println("Error: tacoOrder or list of tacos equal null!");
        }

        return "redirect:/orders/current";
    }

    @GetMapping("/edit")
    public String editTaco(@RequestParam("index") int index,
                           @ModelAttribute TacoOrder tacoOrder,
                           Model model) {
        if (index >= 0 && index < tacoOrder.getTacos().size()) {
            Taco tacoToEdit = tacoOrder.getTacos().remove(index);
            model.addAttribute("taco", tacoToEdit);
        } else {
            model.addAttribute("taco", new Taco());
        }

        return "design";
    }


    @PostMapping
    public String processTaco(@Valid Taco taco, Errors errors,
                              @ModelAttribute TacoOrder tacoOrder) {
        if (errors.hasErrors()) {
            return "design";
        }
        tacoOrder.addTaco(taco);
        log.info("Processing taco: {}", taco);
        return "redirect:/orders/current";
    }
}