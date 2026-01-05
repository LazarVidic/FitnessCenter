package com.fitnesscenter.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fitnesscenter.dtos.MemberDto;
import com.fitnesscenter.models.Member;
import com.fitnesscenter.models.Roll;
import com.fitnesscenter.repositories.MemberRepository;
import com.fitnesscenter.repositories.ReservationRepository;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReservationRepository reservationRepository; // trenutno ti ne treba

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public Member getMemberById(int member_id) {
        return memberRepository.findById(member_id);
    }

    public Member createMemberAdmin(MemberDto memberDto) {
        Member member = new Member(
                memberDto.getMemberName(),
                memberDto.getMemberSurname(),
                memberDto.getPhone(),
                Roll.ROLE_ADMIN,
                memberDto.getEmail(),
                passwordEncoder.encode(memberDto.getPassword()),
                memberDto.getUsername()
        );
        return memberRepository.save(member);
    }

    public Member createMemberSeller(MemberDto memberDto) {
        Member member = new Member(
                memberDto.getMemberName(),
                memberDto.getMemberSurname(),
                memberDto.getPhone(),
                Roll.ROLE_SELLER,
                memberDto.getEmail(),
                passwordEncoder.encode(memberDto.getPassword()),
                memberDto.getUsername()
        );
        return memberRepository.save(member);
    }

    public Member createMemberUser(MemberDto memberDto) {
        Member member = new Member(
                memberDto.getMemberName(),
                memberDto.getMemberSurname(),
                memberDto.getPhone(),
                Roll.ROLE_USER,
                memberDto.getEmail(),
                passwordEncoder.encode(memberDto.getPassword()),
                memberDto.getUsername()
        );
        return memberRepository.save(member);
    }

    public Member updateMember(int member_id, MemberDto memberDto) {
        Member member = memberRepository.findById(member_id);
        if (member == null) {
            return null;
        }

        // update basic fields (samo ako su poslati, da ne pregaziš null-ovima)
        if (memberDto.getMemberName() != null && !memberDto.getMemberName().isBlank()) {
            member.setMemberName(memberDto.getMemberName());
        }
        if (memberDto.getMemberSurname() != null && !memberDto.getMemberSurname().isBlank()) {
            member.setMemberSurname(memberDto.getMemberSurname());
        }
        if (memberDto.getPhone() != null && !memberDto.getPhone().isBlank()) {
            member.setPhone(memberDto.getPhone());
        }
        if (memberDto.getUsername() != null && !memberDto.getUsername().isBlank()) {
            member.setUsername(memberDto.getUsername());
        }

        // email: validacija + unique check
        if (memberDto.getEmail() != null && !memberDto.getEmail().isBlank()) {
            Member existing = memberRepository.findByEmail(memberDto.getEmail()).orElse(null);

          
            if (existing != null && existing.getMemberId() != member_id) {
                return null; 
            }

            member.setEmail(memberDto.getEmail());
        }

        // password: menjaj samo ako je poslato i nije prazno
        if (memberDto.getPassword() != null && !memberDto.getPassword().isBlank()) {
            member.setPassword(passwordEncoder.encode(memberDto.getPassword()));
        }

        return memberRepository.save(member);
    }

    public Member deleteMember(int member_id) {
    	Member member = memberRepository.findById(member_id);

		if (member == null) {
			return null;
		}

		memberRepository.delete(member);

		return member;
	}
}
