package com.ontheworld.pos.mapper;

import com.ontheworld.pos.dto.branch.BranchRequest;
import com.ontheworld.pos.dto.branch.BranchResponse;
import com.ontheworld.pos.entity.Branch;
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
public class BranchMapperImpl implements BranchMapper {

    @Override
    public BranchResponse toResponse(Branch branch) {
        if ( branch == null ) {
            return null;
        }

        BranchResponse branchResponse = new BranchResponse();

        branchResponse.setAddressEn( branch.getAddressEn() );
        branchResponse.setAddressTh( branch.getAddressTh() );
        branchResponse.setCreatedAt( branch.getCreatedAt() );
        branchResponse.setId( branch.getId() );
        branchResponse.setNameEn( branch.getNameEn() );
        branchResponse.setNameTh( branch.getNameTh() );
        branchResponse.setUpdatedAt( branch.getUpdatedAt() );

        return branchResponse;
    }

    @Override
    public List<BranchResponse> toResponseList(List<Branch> branches) {
        if ( branches == null ) {
            return null;
        }

        List<BranchResponse> list = new ArrayList<BranchResponse>( branches.size() );
        for ( Branch branch : branches ) {
            list.add( toResponse( branch ) );
        }

        return list;
    }

    @Override
    public Branch toEntity(BranchRequest request) {
        if ( request == null ) {
            return null;
        }

        Branch branch = new Branch();

        branch.setAddressEn( request.getAddressEn() );
        branch.setAddressTh( request.getAddressTh() );
        branch.setNameEn( request.getNameEn() );
        branch.setNameTh( request.getNameTh() );

        return branch;
    }

    @Override
    public void updateEntity(BranchRequest request, Branch branch) {
        if ( request == null ) {
            return;
        }

        branch.setAddressEn( request.getAddressEn() );
        branch.setAddressTh( request.getAddressTh() );
        branch.setNameEn( request.getNameEn() );
        branch.setNameTh( request.getNameTh() );
    }
}
