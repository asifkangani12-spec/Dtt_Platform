//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.dtt.repo;

import com.dtt.model.GenericApiEndpoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiEndpointRepository extends JpaRepository<GenericApiEndpoint, Long> {

}
