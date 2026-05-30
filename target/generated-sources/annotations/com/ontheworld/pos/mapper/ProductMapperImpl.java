package com.ontheworld.pos.mapper;

import com.ontheworld.pos.dto.product.ProductRequest;
import com.ontheworld.pos.dto.product.ProductResponse;
import com.ontheworld.pos.entity.Category;
import com.ontheworld.pos.entity.Product;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2569-05-30T12:08:07+0700",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class ProductMapperImpl implements ProductMapper {

    @Override
    public ProductResponse toResponse(Product product) {
        if ( product == null ) {
            return null;
        }

        ProductResponse productResponse = new ProductResponse();

        productResponse.setCategoryId( productCategoryId( product ) );
        productResponse.setCategoryNameTh( productCategoryNameTh( product ) );
        productResponse.setCategoryNameEn( productCategoryNameEn( product ) );
        productResponse.setBarcode( product.getBarcode() );
        productResponse.setCost( product.getCost() );
        productResponse.setCreatedAt( product.getCreatedAt() );
        productResponse.setId( product.getId() );
        productResponse.setImageUrl( product.getImageUrl() );
        productResponse.setNameEn( product.getNameEn() );
        productResponse.setNameTh( product.getNameTh() );
        productResponse.setPrice( product.getPrice() );
        productResponse.setReorderThreshold( product.getReorderThreshold() );
        productResponse.setSku( product.getSku() );
        productResponse.setUpdatedAt( product.getUpdatedAt() );

        return productResponse;
    }

    @Override
    public List<ProductResponse> toResponseList(List<Product> products) {
        if ( products == null ) {
            return null;
        }

        List<ProductResponse> list = new ArrayList<ProductResponse>( products.size() );
        for ( Product product : products ) {
            list.add( toResponse( product ) );
        }

        return list;
    }

    @Override
    public Product toEntity(ProductRequest request) {
        if ( request == null ) {
            return null;
        }

        Product product = new Product();

        product.setBarcode( request.getBarcode() );
        product.setCost( request.getCost() );
        product.setNameEn( request.getNameEn() );
        product.setNameTh( request.getNameTh() );
        product.setPrice( request.getPrice() );
        product.setReorderThreshold( request.getReorderThreshold() );
        product.setSku( request.getSku() );

        return product;
    }

    @Override
    public void updateEntity(ProductRequest request, Product product) {
        if ( request == null ) {
            return;
        }

        product.setBarcode( request.getBarcode() );
        product.setCost( request.getCost() );
        product.setNameEn( request.getNameEn() );
        product.setNameTh( request.getNameTh() );
        product.setPrice( request.getPrice() );
        product.setReorderThreshold( request.getReorderThreshold() );
        product.setSku( request.getSku() );
    }

    private UUID productCategoryId(Product product) {
        if ( product == null ) {
            return null;
        }
        Category category = product.getCategory();
        if ( category == null ) {
            return null;
        }
        UUID id = category.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String productCategoryNameTh(Product product) {
        if ( product == null ) {
            return null;
        }
        Category category = product.getCategory();
        if ( category == null ) {
            return null;
        }
        String nameTh = category.getNameTh();
        if ( nameTh == null ) {
            return null;
        }
        return nameTh;
    }

    private String productCategoryNameEn(Product product) {
        if ( product == null ) {
            return null;
        }
        Category category = product.getCategory();
        if ( category == null ) {
            return null;
        }
        String nameEn = category.getNameEn();
        if ( nameEn == null ) {
            return null;
        }
        return nameEn;
    }
}
