package com.hr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hr.entity.Compose;

@Repository
public interface ComposeRepo extends JpaRepository<Compose, Integer>{

	public List<Compose> findByParentUkid(Integer parentUkid);
	
	/*@Query("SELECT COUNT(c) FROM Compose c WHERE c.status = :status")
	int countByStatus(@Param("status") String status);

	@Query("SELECT COUNT(c) FROM Compose c")
	int countAll();*/

}
