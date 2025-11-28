package com.imran.edcassistant;

import com.imran.edcassistant.entity.AssetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface AssetRepository extends JpaRepository<AssetEntity, Long> {

    List<AssetEntity> findAllByAssetIdIn(Collection<String> assetIds);
    AssetEntity getAssetByAssetId(String assetId);

}
