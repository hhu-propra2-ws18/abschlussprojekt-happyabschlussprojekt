package com.propra.happybay.Repository;

import com.propra.happybay.Model.Geraet;
import com.propra.happybay.Model.Notification;
import com.propra.happybay.Model.TransferRequest;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TransferRequestRepository extends CrudRepository<TransferRequest,Long> {
   List<TransferRequest> findAll();

}
