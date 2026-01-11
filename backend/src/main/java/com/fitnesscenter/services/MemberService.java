package com.fitnesscenter.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fitnesscenter.dtos.MemberDto;
import com.fitnesscenter.models.Location;
import com.fitnesscenter.models.Member;
import com.fitnesscenter.models.Roll;
import com.fitnesscenter.repositories.LocationRepository;
import com.fitnesscenter.repositories.MemberRepository;

@org.springframework.stereotype.Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public Member getMemberById(int member_id) {
        Optional<Member> opt = memberRepository.findById(member_id);
        if (opt.isPresent()) return opt.get();
        return null;
    }

    private Location resolveLocation(int locationId) {
        if (locationId <= 0) return null;

        Optional<Location> opt = locationRepository.findById(locationId);
        if (opt.isPresent()) return opt.get();
        return null;
    }

    public Member createMemberAdmin(MemberDto dto) {
        Location loc = resolveLocation(dto.getLocationId());
        if (dto.getLocationId() > 0 && loc == null) return null;

        Member member = new Member(
                dto.getMemberName(),
                dto.getMemberSurname(),
                dto.getPhone(),
                Roll.ROLE_ADMIN,
                dto.getEmail(),
                passwordEncoder.encode(dto.getPassword()),
                dto.getUsername(),
                loc
        );
        return memberRepository.save(member);
    }

    public Member createMemberSeller(MemberDto dto) {
        Location loc = resolveLocation(dto.getLocationId());
        if (dto.getLocationId() > 0 && loc == null) return null;

        Member member = new Member(
                dto.getMemberName(),
                dto.getMemberSurname(),
                dto.getPhone(),
                Roll.ROLE_SELLER,
                dto.getEmail(),
                passwordEncoder.encode(dto.getPassword()),
                dto.getUsername(),
                loc
        );
        return memberRepository.save(member);
    }

    public Member createMemberUser(MemberDto dto) {
        Location loc = resolveLocation(dto.getLocationId());
        if (dto.getLocationId() > 0 && loc == null) return null;

        Member member = new Member(
                dto.getMemberName(),
                dto.getMemberSurname(),
                dto.getPhone(),
                Roll.ROLE_USER,
                dto.getEmail(),
                passwordEncoder.encode(dto.getPassword()),
                dto.getUsername(),
                loc
        );
        return memberRepository.save(member);
    }

    public Member updateMember(int member_id, MemberDto dto) {
        Optional<Member> optMember = memberRepository.findById(member_id);
        Member member = null;
        if (optMember.isPresent()) member = optMember.get();
        if (member == null) return null;

        if (dto.getMemberName() != null && !dto.getMemberName().isBlank()) member.setMemberName(dto.getMemberName());
        if (dto.getMemberSurname() != null && !dto.getMemberSurname().isBlank()) member.setMemberSurname(dto.getMemberSurname());
        if (dto.getPhone() != null && !dto.getPhone().isBlank()) member.setPhone(dto.getPhone());
        if (dto.getUsername() != null && !dto.getUsername().isBlank()) member.setUsername(dto.getUsername());

        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            Member existing = memberRepository.findByEmail(dto.getEmail()).orElse(null);
            if (existing != null && existing.getMemberId() != member_id) return null;
            member.setEmail(dto.getEmail());
        }

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            member.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        if (dto.getLocationId() >= 0) {
            Location loc = resolveLocation(dto.getLocationId());
            if (dto.getLocationId() > 0 && loc == null) return null;
            member.setLocation(loc); // 0 -> null
        }

        return memberRepository.save(member);
    }

    public Member deleteMember(int member_id) {
        Optional<Member> optMember = memberRepository.findById(member_id);
        Member member = null;
        if (optMember.isPresent()) member = optMember.get();
        if (member == null) return null;

        memberRepository.delete(member);
        return member;
    }
}
