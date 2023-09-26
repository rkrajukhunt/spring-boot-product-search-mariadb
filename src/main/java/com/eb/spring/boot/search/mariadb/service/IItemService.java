package com.eb.spring.boot.search.mariadb.service;

import com.eb.spring.boot.search.mariadb.persistance.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public interface IItemService {
    Item createItem(Item item);

    ResponseEntity<Item> updateItem(Long id, Item updatedItem);

    ResponseEntity<Void> deleteById(Long id);

    ResponseEntity<Item> getItemById(Long id);

    Page<Item> searchItems(String searchQuery, String sortBy, String sortDirection, int page, int size);

    Page<Item> searchItems(String name, String description, String category, Integer stockQuantity, LocalDate manufactureDate, String sortBy, String sortDirection, Pageable pageable);

    ResponseEntity<List<Item>> getItems();
}
