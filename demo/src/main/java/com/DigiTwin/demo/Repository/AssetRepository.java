package com.DigiTwin.demo.Repository;

import java.util.*;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.DigiTwin.demo.Model.Asset;

public interface AssetRepository extends MongoRepository<Asset, String> {
    List<Asset> findByProjectId(String projectId); List<Asset> findBySiteId(String siteId); List<Asset> findByTypeIgnoreCase(String type);
}
