package com.galitianu.mealmonitorbackend.products.api;

import com.galitianu.mealmonitorbackend.common.api.BaseResource;
import com.galitianu.mealmonitorbackend.products.api.dto.ProductDto;
import com.galitianu.mealmonitorbackend.products.mapper.ProductMapper;
import com.galitianu.mealmonitorbackend.products.service.ProductService;
import com.galitianu.mealmonitorbackend.products.service.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/products")
public class ProductResource extends BaseResource {
    private final ProductService productService;
    private final ProductMapper productMapper;

    @PostMapping()
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto productDto)
    {
        Product product = productService.createProduct(productDto.getBarcode(), productDto.getName(), productDto.getBrand(),
                productDto.getRecommendedQuantity(), productDto.getCaloriesPerCent(), productDto.getProteinsPerCent(),
                productDto.getCarbsPerCent(), productDto.getFatsPerCent());
        return new ResponseEntity<>(productMapper.mapToDto(product), HttpStatus.OK);
    }

    @GetMapping("/{barCode}")
    public ResponseEntity<ProductDto> getProductByBarcode(@PathVariable String barCode) {
        return new ResponseEntity<>(productMapper.mapToDto(productService.getProductByBarcode(barCode)), HttpStatus.OK);
    }

    @GetMapping("/search/{query}") // TODO debounce?
    public ResponseEntity<List<ProductDto>> getProductsBySearch(@PathVariable String query) {
        List<Product> products = productService.getProductsBySearch(query);
        return new ResponseEntity<>(products.stream().map(productMapper::mapToDto).toList(), HttpStatus.OK);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<String> deleteMeal(@PathVariable UUID productId) {
        Optional<Product> product = productService.findById(productId);
        if (product.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        productService.delete(product.get());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
