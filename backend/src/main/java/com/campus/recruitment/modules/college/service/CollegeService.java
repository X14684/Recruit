package com.campus.recruitment.modules.college.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.recruitment.modules.college.entity.College;

import java.util.Map;

public interface CollegeService extends IService<College> {
    Map<String, Object> login(String username, String password);
}
