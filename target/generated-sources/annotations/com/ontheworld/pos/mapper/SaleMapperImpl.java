package com.ontheworld.pos.mapper;

import com.ontheworld.pos.dto.sale.SaleResponse;
import com.ontheworld.pos.entity.Branch;
import com.ontheworld.pos.entity.Customer;
import com.ontheworld.pos.entity.Sale;
import com.ontheworld.pos.entity.UserAccount;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2569-05-30T12:08:07+0700",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class SaleMapperImpl implements SaleMapper {

    @Override
    public SaleResponse toResponse(Sale sale) {
        if ( sale == null ) {
            return null;
        }

        SaleResponse saleResponse = new SaleResponse();

        saleResponse.setBranchId( saleBranchId( sale ) );
        saleResponse.setBranchNameTh( saleBranchNameTh( sale ) );
        saleResponse.setBranchNameEn( saleBranchNameEn( sale ) );
        saleResponse.setCashierId( saleCashierId( sale ) );
        saleResponse.setCashierName( saleCashierUsername( sale ) );
        saleResponse.setCustomerId( saleCustomerId( sale ) );
        saleResponse.setCreatedAt( sale.getCreatedAt() );
        saleResponse.setDiscount( sale.getDiscount() );
        saleResponse.setId( sale.getId() );
        saleResponse.setReceiptNumber( sale.getReceiptNumber() );
        saleResponse.setSubTotal( sale.getSubTotal() );
        saleResponse.setTax( sale.getTax() );
        saleResponse.setTotal( sale.getTotal() );

        return saleResponse;
    }

    private UUID saleBranchId(Sale sale) {
        if ( sale == null ) {
            return null;
        }
        Branch branch = sale.getBranch();
        if ( branch == null ) {
            return null;
        }
        UUID id = branch.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String saleBranchNameTh(Sale sale) {
        if ( sale == null ) {
            return null;
        }
        Branch branch = sale.getBranch();
        if ( branch == null ) {
            return null;
        }
        String nameTh = branch.getNameTh();
        if ( nameTh == null ) {
            return null;
        }
        return nameTh;
    }

    private String saleBranchNameEn(Sale sale) {
        if ( sale == null ) {
            return null;
        }
        Branch branch = sale.getBranch();
        if ( branch == null ) {
            return null;
        }
        String nameEn = branch.getNameEn();
        if ( nameEn == null ) {
            return null;
        }
        return nameEn;
    }

    private UUID saleCashierId(Sale sale) {
        if ( sale == null ) {
            return null;
        }
        UserAccount cashier = sale.getCashier();
        if ( cashier == null ) {
            return null;
        }
        UUID id = cashier.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String saleCashierUsername(Sale sale) {
        if ( sale == null ) {
            return null;
        }
        UserAccount cashier = sale.getCashier();
        if ( cashier == null ) {
            return null;
        }
        String username = cashier.getUsername();
        if ( username == null ) {
            return null;
        }
        return username;
    }

    private UUID saleCustomerId(Sale sale) {
        if ( sale == null ) {
            return null;
        }
        Customer customer = sale.getCustomer();
        if ( customer == null ) {
            return null;
        }
        UUID id = customer.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
