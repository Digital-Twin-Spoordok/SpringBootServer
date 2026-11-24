package com.DigiTwin.demo.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.DigiTwin.demo.Model.DevSettings;

public interface DevSettingsRepository extends MongoRepository<DevSettings, String> {

}
