package com.api.conectaComunidade.communityservice.repository;

import com.api.conectaComunidade.communityservice.entity.CommunityService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityServiceRepository extends JpaRepository<CommunityService,Long> {
}
