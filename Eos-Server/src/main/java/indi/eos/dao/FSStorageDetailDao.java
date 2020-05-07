package indi.eos.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import indi.eos.entities.FsStorageDetailEntity;

@Repository
public interface FSStorageDetailDao extends JpaRepository<FsStorageDetailEntity, Long> {
    @Query("select e from FsStorageDetailEntity e where e.refID = :id")
    FsStorageDetailEntity findOneByRefID(@Param("id") Long id);
}
