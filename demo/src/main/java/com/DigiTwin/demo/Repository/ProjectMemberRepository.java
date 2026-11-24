package com.DigiTwin.demo.Repository;

import java.util.*;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.DigiTwin.demo.Model.ProjectMember;

public interface ProjectMemberRepository extends MongoRepository<ProjectMember, String> {
    List<ProjectMember> findByProjectId(String projectId); List<ProjectMember> findByUserId(String userId); Optional<ProjectMember> findByProjectIdAndUserId(String projectId, String userId);
}
