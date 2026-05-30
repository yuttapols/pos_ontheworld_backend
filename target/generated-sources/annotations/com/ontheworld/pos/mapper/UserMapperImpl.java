package com.ontheworld.pos.mapper;

import com.ontheworld.pos.dto.user.UserResponse;
import com.ontheworld.pos.entity.Branch;
import com.ontheworld.pos.entity.UserAccount;
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
public class UserMapperImpl implements UserMapper {

    @Override
    public UserResponse toResponse(UserAccount user) {
        if ( user == null ) {
            return null;
        }

        UserResponse userResponse = new UserResponse();

        userResponse.setBranchId( userBranchId( user ) );
        userResponse.setBranchNameTh( userBranchNameTh( user ) );
        userResponse.setBranchNameEn( userBranchNameEn( user ) );
        userResponse.setRole( roleToString( user.getRole() ) );
        userResponse.setCreatedAt( user.getCreatedAt() );
        userResponse.setEmail( user.getEmail() );
        userResponse.setEnabled( user.isEnabled() );
        userResponse.setId( user.getId() );
        userResponse.setUsername( user.getUsername() );

        return userResponse;
    }

    @Override
    public List<UserResponse> toResponseList(List<UserAccount> users) {
        if ( users == null ) {
            return null;
        }

        List<UserResponse> list = new ArrayList<UserResponse>( users.size() );
        for ( UserAccount userAccount : users ) {
            list.add( toResponse( userAccount ) );
        }

        return list;
    }

    private UUID userBranchId(UserAccount userAccount) {
        if ( userAccount == null ) {
            return null;
        }
        Branch branch = userAccount.getBranch();
        if ( branch == null ) {
            return null;
        }
        UUID id = branch.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String userBranchNameTh(UserAccount userAccount) {
        if ( userAccount == null ) {
            return null;
        }
        Branch branch = userAccount.getBranch();
        if ( branch == null ) {
            return null;
        }
        String nameTh = branch.getNameTh();
        if ( nameTh == null ) {
            return null;
        }
        return nameTh;
    }

    private String userBranchNameEn(UserAccount userAccount) {
        if ( userAccount == null ) {
            return null;
        }
        Branch branch = userAccount.getBranch();
        if ( branch == null ) {
            return null;
        }
        String nameEn = branch.getNameEn();
        if ( nameEn == null ) {
            return null;
        }
        return nameEn;
    }
}
