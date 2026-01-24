package com.module5.team2.security.jwt;

import com.module5.team2.entity.UserEntity;
import com.module5.team2.entity.UserEntity.Status;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


public class CustomUserDetails implements UserDetails {

    private final UserEntity user;

    public CustomUserDetails(UserEntity user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }


    @Override
    public boolean isEnabled() {
        return user.getStatus() == Status.active;
    }


    @Override
    public boolean isAccountNonLocked() {
        return user.getStatus() == Status.active;
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }


    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }


    public UserEntity getUserEntity() {
        return user;
    }


    public boolean isBanned() {
        return user.getStatus() == Status.banned;
    }
}
