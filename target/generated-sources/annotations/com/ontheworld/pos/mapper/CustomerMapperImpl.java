package com.ontheworld.pos.mapper;

import com.ontheworld.pos.dto.customer.CustomerRequest;
import com.ontheworld.pos.dto.customer.CustomerResponse;
import com.ontheworld.pos.entity.Customer;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2569-05-30T12:08:07+0700",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class CustomerMapperImpl implements CustomerMapper {

    @Override
    public CustomerResponse toResponse(Customer customer) {
        if ( customer == null ) {
            return null;
        }

        CustomerResponse customerResponse = new CustomerResponse();

        customerResponse.setCreatedAt( customer.getCreatedAt() );
        customerResponse.setEmail( customer.getEmail() );
        customerResponse.setId( customer.getId() );
        customerResponse.setLoyaltyPoints( customer.getLoyaltyPoints() );
        customerResponse.setNameEn( customer.getNameEn() );
        customerResponse.setNameTh( customer.getNameTh() );
        customerResponse.setPhone( customer.getPhone() );
        customerResponse.setUpdatedAt( customer.getUpdatedAt() );

        return customerResponse;
    }

    @Override
    public Customer toEntity(CustomerRequest request) {
        if ( request == null ) {
            return null;
        }

        Customer customer = new Customer();

        customer.setEmail( request.getEmail() );
        customer.setNameEn( request.getNameEn() );
        customer.setNameTh( request.getNameTh() );
        customer.setPhone( request.getPhone() );

        return customer;
    }

    @Override
    public void updateEntity(CustomerRequest request, Customer customer) {
        if ( request == null ) {
            return;
        }

        customer.setEmail( request.getEmail() );
        customer.setNameEn( request.getNameEn() );
        customer.setNameTh( request.getNameTh() );
        customer.setPhone( request.getPhone() );
    }
}
