package com.example.jwtserver.repository;

import com.example.jwtserver.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * MemberRepository
 */
public interface MemberRepository extends JpaRepository<Member, String> {

	Member findMemberById(String id);
}
