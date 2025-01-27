package com.talentica.branch_management.repository;

import com.talentica.branch_management.entity.PinCodeMaster;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PinCodeMasterRepository extends MongoRepository<PinCodeMaster,String> {

    PinCodeMaster findByPinCode(String pinCode);
}
