package com.campus.recruitment.modules.company.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.recruitment.modules.company.dto.CompanyRegisterDTO;
import com.campus.recruitment.modules.company.entity.Company;

import java.util.Map;

public interface CompanyService extends IService<Company> {
    void register(CompanyRegisterDTO dto);
    Map<String, Object> login(String username, String password);
    void updateProfile(Company company);
    void audit(Long id, Integer status, String remark);
}
