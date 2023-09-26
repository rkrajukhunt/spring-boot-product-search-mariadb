package com.eb.spring.boot.search.mariadb.service.Impl;

import com.eb.spring.boot.search.mariadb.persistance.Item;
import com.eb.spring.boot.search.mariadb.service.IItemService;
import com.eb.spring.boot.search.mariadb.persistance.repository.ItemRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ItemServiceImpl implements IItemService {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ItemRepository itemRepository;

    @Override
    public Item createItem(Item item) {
        return itemRepository.save(item);
    }

    @Override
    public ResponseEntity<Item> updateItem(Long id, Item updatedItem) {
        Optional<Item> existingItem = itemRepository.findById(id);
        if (existingItem.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Item item = existingItem.get();
        item.setName(updatedItem.getName());
        item.setPrice(updatedItem.getPrice());
        itemRepository.save(item);
        return ResponseEntity.ok(item);
    }

    @Override
    public ResponseEntity<Void> deleteById(Long id) {
        itemRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Item> getItemById(Long id) {
        Optional<Item> item = itemRepository.findById(id);
        return item.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @Override
    public Page<Item> searchItems(String searchQuery, String sortBy, String sortDirection, int page, int size) {
        String baseQuery = "SELECT * FROM items WHERE MATCH(name, description, category) AGAINST (:searchQuery IN NATURAL LANGUAGE MODE)";
        String query = baseQuery + " ORDER BY " + sortBy + " " + sortDirection;

        List items = entityManager
                .createNativeQuery(query, Item.class)
                .setParameter("searchQuery", searchQuery)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();

        return new PageImpl<>(items, PageRequest.of(page, size), getTotalCount(baseQuery, searchQuery));
    }

    @Override
    public Page<Item> searchItems(String name, String description, String category, Integer stockQuantity, LocalDate manufactureDate, String sortBy, String sortDirection, Pageable pageable) {
        Specification<Item> spec = Specification.where(null);

        if (name != null) {
            spec = spec.and((root, query, cb) ->
                    cb.like(root.get("name"), "%" + name + "%"));
        }

        if (description != null) {
            spec = spec.and((root, query, cb) ->
                    cb.like(root.get("description"), "%" + description + "%"));
        }

        if (category != null) {
            spec = spec.and((root, query, cb) ->
                    cb.like(root.get("category"), "%" + category + "%"));
        }

        if (stockQuantity != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("stockQuantity"), stockQuantity));
        }

        if (manufactureDate != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("manufactureDate"), manufactureDate));
        }

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        return itemRepository.findAll(spec, pageable);
    }

    @Override
    public ResponseEntity<List<Item>> getItems() {
        List<Item> items = itemRepository.findAll();
        return ResponseEntity.ok(items);
    }

    private long getTotalCount(String baseQuery, String searchQuery) {
        String countQuery = "SELECT COUNT(*) FROM (" + baseQuery + ") AS countQuery";
        return (long) entityManager
                .createNativeQuery(countQuery)
                .setParameter("searchQuery", searchQuery)
                .getSingleResult();
    }
}
