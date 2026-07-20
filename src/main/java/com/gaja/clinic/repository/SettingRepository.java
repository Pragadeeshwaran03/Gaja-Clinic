package com.gaja.clinic.repository;

import com.gaja.clinic.entity.Setting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SettingRepository extends JpaRepository<Setting, Integer> {

    Optional<Setting> findByKey(String key);

    List<Setting> findByKeyIn(List<String> keys);
}
