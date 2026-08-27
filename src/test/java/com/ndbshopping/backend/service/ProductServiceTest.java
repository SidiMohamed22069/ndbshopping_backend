package com.ndbshopping.backend.service;

import com.ndbshopping.backend.entity.enums.ProductSource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductServiceTest {

    @Test
    void detectSource_facebook() {
        assertEquals(ProductSource.FACEBOOK, ProductService.detectSource("https://www.facebook.com/marketplace/item/1"));
    }

    @Test
    void detectSource_alibaba() {
        assertEquals(ProductSource.ALIBABA, ProductService.detectSource("https://www.alibaba.com/product-detail/foo.html"));
    }

    @Test
    void detectSource_other() {
        assertEquals(ProductSource.AUTRE, ProductService.detectSource("https://example.com/item/1"));
    }
}
