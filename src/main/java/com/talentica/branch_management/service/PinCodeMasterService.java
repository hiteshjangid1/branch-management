package com.talentica.branch_management.service;

import com.talentica.branch_management.entity.PinCodeMaster;
import com.talentica.branch_management.repository.PinCodeMasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PinCodeMasterService {

    @Autowired
    private PinCodeMasterRepository pinCodeMasterRepository;

    public PinCodeMaster fetchPinCodeMaster(String pinCode){
        return pinCodeMasterRepository.findByPinCode(pinCode);
    }
}
