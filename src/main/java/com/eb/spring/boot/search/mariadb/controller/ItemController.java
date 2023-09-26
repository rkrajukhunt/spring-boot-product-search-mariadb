package com.eb.spring.boot.search.mariadb.controller;

import com.eb.spring.boot.search.mariadb.persistance.Item;
import com.eb.spring.boot.search.mariadb.service.IItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/item")
public class ItemController {

    @Autowired
    public IItemService itemService;

    @PostMapping("/")
    public Item createItem(@RequestBody Item item) {
        return itemService.createItem(item);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Item> updateItem(@PathVariable Long id, @RequestBody Item updatedItem) {
        return itemService.updateItem(id, updatedItem);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        return itemService.deleteById(id);
    }

    @GetMapping("/")
    public ResponseEntity<List<Item>> getItems() {
        return itemService.getItems();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Long id) {
        return itemService.getItemById(id);
    }

    @GetMapping("/search")
    public Page<Item> searchItems(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer stockQuantity,
            @RequestParam(required = false) LocalDate manufactureDate,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortDirection,
            @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {
        return itemService.searchItems(name, description, category, stockQuantity, manufactureDate, sortBy, sortDirection, pageable);
    }

    @GetMapping("/full-text-search")
    public Page<Item> searchItems(
            @RequestParam(required = false) String searchQuery,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortDirection,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return itemService.searchItems(searchQuery, sortBy, sortDirection, page, size);
    }
}
