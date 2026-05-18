package com.github.kaylves.test.infra.core.builder;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PageBuilder<T> {

    private List<T> content = new ArrayList<>();
    private int page = 0;
    private int size = 10;
    private long total = -1;
    private Sort sort;

    public PageBuilder<T> content(@SuppressWarnings("unchecked") T... items) {
        this.content = Arrays.asList(items);
        return this;
    }

    public PageBuilder<T> content(List<T> content) {
        if (content == null) {
            throw new IllegalArgumentException("content must not be null");
        }
        this.content = content;
        return this;
    }

    public PageBuilder<T> page(int page) {
        this.page = page;
        return this;
    }

    public PageBuilder<T> size(int size) {
        this.size = size;
        return this;
    }

    public PageBuilder<T> total(long total) {
        this.total = total;
        return this;
    }

    public PageBuilder<T> sort(Sort sort) {
        this.sort = sort;
        return this;
    }

    public PageBuilder<T> sort(String... properties) {
        this.sort = Sort.by(properties);
        return this;
    }

    public Page<T> build() {
        long actualTotal = total >= 0 ? total : content.size();
        Pageable pageable = sort != null ? PageRequest.of(page, size, sort) : PageRequest.of(page, size);
        return new PageImpl<>(Collections.unmodifiableList(content), pageable, actualTotal);
    }

    public static <T> PageBuilder<T> builder() {
        return new PageBuilder<>();
    }

    public static <T> Page<T> of(List<T> content) {
        return PageBuilder.<T>builder().content(content).build();
    }

    public static <T> Page<T> of(@SuppressWarnings("unchecked") T... items) {
        return PageBuilder.<T>builder().content(items).build();
    }

    public static <T> Page<T> empty() {
        return PageBuilder.<T>builder().build();
    }
}
