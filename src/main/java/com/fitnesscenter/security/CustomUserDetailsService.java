package com.fitnesscenter.security;

import java.util.List;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.fitnesscenter.models.Member;
import com.fitnesscenter.repositories.MemberRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public CustomUserDetailsService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member m = memberRepository.findByEmail(email)
        		.orElseThrow(() -> new UsernameNotFoundException("Member not found: " + email));

        // roll je enum: ROLE_USER / ROLE_ADMIN / ROLE_SELLER
        return org.springframework.security.core.userdetails.User
                .withUsername(m.getEmail())
                .password(m.getPassword())
                .authorities(m.getRoll().name()) // ROLE_USER / ROLE_ADMIN / ROLE_SELLER
                .build();
    }
}
