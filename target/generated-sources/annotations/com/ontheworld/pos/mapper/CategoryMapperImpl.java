package com.ontheworld.pos.mapper;

import com.ontheworld.pos.dto.product.CategoryRequest;
import com.ontheworld.pos.dto.product.CategoryResponse;
import com.ontheworld.pos.entity.Category;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2569-05-25T20:34:15+0700",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class CategoryMapperImpl implements CategoryMapper {

    @Override
    public CategoryResponse toResponse(Category category) {
        if ( category == null ) {
            return null;
        }

        CategoryResponse categoryResponse = new CategoryResponse();

        categoryResponse.setCreatedAt( category.getCreatedAt() );
        categoryResponse.setDescriptionEn( category.getDescriptionEn() );
        categoryResponse.setDescriptionTh( category.getDescriptionTh() );
        categoryResponse.setId( category.getId() );
        categoryResponse.setNameEn( category.getNameEn() );
        categoryResponse.setNameTh( category.getNameTh() );
        categoryResponse.setUpdatedAt( category.getUpdatedAt() );

        return categoryResponse;
    }

    @Override
    public List<CategoryResponse> toResponseList(List<Category> categories) {
        if ( categories == null ) {
            return null;
        }

        List<CategoryResponse> list = new ArrayList<CategoryResponse>( categories.size() );
        for ( Category category : categories ) {
            list.add( toResponse( category ) );
        }

        return list;
    }

    @Override
    public Category toEntity(CategoryRequest request) {
        if ( request == null ) {
            return null;
        }

        Category category = new Category();

        category.setDescriptionEn( request.getDescriptionEn() );
        category.setDescriptionTh( request.getDescriptionTh() );
        category.setNameEn( request.getNameEn() );
        category.setNameTh( request.getNameTh() );

        return category;
    }

    @Override
    public void updateEntity(CategoryRequest request, Category category) {
        if ( request == null ) {
            return;
        }

        category.setDescriptionEn( request.getDescriptionEn() );
        category.setDescriptionTh( request.getDescriptionTh() );
        category.setNameEn( request.getNameEn() );
        category.setNameTh( request.getNameTh() );
    }
}
