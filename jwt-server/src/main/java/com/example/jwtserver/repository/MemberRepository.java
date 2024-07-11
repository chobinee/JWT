package com.example.jwtserver.repository;

import com.example.jwtserver.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * MemberRepository
 */
public interface MemberRepository extends JpaRepository<Member, String> {

    /**
     * db에서 id로 Member 찾음
     * @param id
     * @return
     */
	Member findMemberById(String id);
}
