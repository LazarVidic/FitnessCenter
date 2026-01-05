package com.fitnesscenter.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.fitnesscenter.dtos.MemberDto;
import com.fitnesscenter.models.Member;
import com.fitnesscenter.services.MemberService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/members")
@SecurityRequirement(name = "bearerAuth")
public class MemberController {

	@Autowired
	private MemberService memberService;

	// ADMIN: get all
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping
	@Operation(summary = "Get all members", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<List<Member>> getAllMembers() {
		List<Member> members = memberService.getAllMembers();
		return new ResponseEntity<List<Member>>(members, HttpStatus.OK);
	}

	// ADMIN: get by id
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/{member_id}")
	@Operation(summary = "Get member by ID", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<Member> getMemberById(@PathVariable int member_id) {
		Member member = memberService.getMemberById(member_id);
		if (member == null) {
			return new ResponseEntity<Member>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Member>(member, HttpStatus.OK);
	}

	// ADMIN: create admin
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/create/admin")
	@Operation(summary = "Create member ADMIN", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<Member> createMemberAdmin(@RequestBody MemberDto memberDto) {

		Member member = memberService.createMemberAdmin(memberDto);
		if (member == null) {
			return new ResponseEntity<Member>(HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<Member>(member, HttpStatus.CREATED);
	}

	// ADMIN: create seller
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/create/seller")
	@Operation(summary = "Create member SELLER", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<Member> createMemberSeller(@RequestBody MemberDto memberDto) {

		Member member = memberService.createMemberSeller(memberDto);
		if (member == null) {
			return new ResponseEntity<Member>(HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<Member>(member, HttpStatus.CREATED);
	}

	// ADMIN: create user
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/create/user")
	@Operation(summary = "Create member USER", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<Member> createMemberUser(@RequestBody MemberDto memberDto) {

		Member member = memberService.createMemberUser(memberDto);
		if (member == null) {
			return new ResponseEntity<Member>(HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<Member>(member, HttpStatus.CREATED);
	}

	// ADMIN: update any member by id
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/update/{member_id}")
	@Operation(summary = "Update member", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<Member> updateMember(@PathVariable int member_id, @RequestBody MemberDto memberDto) {

		Member member = memberService.updateMember(member_id, memberDto);
		if (member == null) {
			return new ResponseEntity<Member>(HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<Member>(member, HttpStatus.OK);
	}

	// ADMIN: delete
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/delete/{member_id}")
	@Operation(summary = "Delete member", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<Member> deleteMember(@PathVariable int member_id) {

		Member member = memberService.deleteMember(member_id);
		if (member == null) {
			return new ResponseEntity<Member>(HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<Member>(member, HttpStatus.OK);
	}
}
