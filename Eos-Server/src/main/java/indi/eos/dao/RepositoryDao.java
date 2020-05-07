package indi.eos.dao;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import indi.eos.entities.RepositoryEntity;

@Repository
public interface RepositoryDao extends JpaRepository<RepositoryEntity, Long> {
    @Query("select e from RepositoryEntity e where e.name = :name")
    RepositoryEntity findOneByName(@Param("name") String name);

    @Query("select count(e) > 0 from RepositoryEntity e where e.name = :name")
    boolean existByName(@Param("name") String name);
}
