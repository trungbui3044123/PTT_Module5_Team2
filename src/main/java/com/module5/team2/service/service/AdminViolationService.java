package com.module5.team2.service.service;

import com.module5.team2.dto.response.ViolationResponse;
import com.module5.team2.entity.Note;
import com.module5.team2.entity.UserEntity;

import java.util.List;

public interface AdminViolationService {
    List<UserEntity> getAllStaff();

    List<ViolationResponse> getViolationsByStaff(Long staffId);
}
