package com.campus.recruitment.modules.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.recruitment.modules.admin.entity.Admin;

import java.util.List;
import java.util.Map;

public interface AdminService extends IService<Admin> {
    Map<String, Object> login(String username, String password);
    Map<String, Object> getStatistics();
    Map<String, Object> getEmploymentStatistics();
    Map<String, Object> getCollegeEmploymentStatistics(Long collegeId);
}

