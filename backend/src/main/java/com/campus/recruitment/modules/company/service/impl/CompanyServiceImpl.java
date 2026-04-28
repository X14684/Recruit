package com.campus.recruitment.modules.company.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.recruitment.common.exception.BusinessException;
import com.campus.recruitment.common.utils.JwtUtils;
import com.campus.recruitment.modules.company.dto.CompanyRegisterDTO;
import com.campus.recruitment.modules.company.entity.Company;
import com.campus.recruitment.modules.company.mapper.CompanyMapper;
import com.campus.recruitment.modules.company.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl extends ServiceImpl<CompanyMapper, Company> implements CompanyService {

    private final JwtUtils jwtUtils;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(CompanyRegisterDTO dto) {
        long count = count(new LambdaQueryWrapper<Company>()
                .eq(Company::getUsername, dto.getUsername()));
        if (count > 0) {
            throw BusinessException.of("用户名已存在");
        }
        Company company = new Company();
        BeanUtils.copyProperties(dto, company);
        company.setPassword(passwordEncoder.encode(dto.getPassword()));
        company.setStatus(1);
        company.setAuditStatus(0);  // 待审核
        save(company);
    }

    @Override
    public Map<String, Object> login(String username, String password) {
        Company company = getOne(new LambdaQueryWrapper<Company>()
                .eq(Company::getUsername, username));

        if (company == null || !passwordEncoder.matches(password, company.getPassword())) {
            throw BusinessException.of("用户名或密码错误");
        }
        if (company.getStatus() == 0) {
            throw BusinessException.of("账号已被禁用");
        }
        if (company.getAuditStatus() == 0) {
            throw BusinessException.of("账号正在审核中，请等待管理员审核");
        }
        if (company.getAuditStatus() == 2) {
            throw BusinessException.of("账号审核未通过：" + company.getAuditRemark());
        }
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", company.getId());
        claims.put("username", company.getUsername());
        claims.put("role", "COMPANY");
        String token = jwtUtils.generateToken(claims);

        // 创建返回信息，隐藏密码
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", company.getId());
        userInfo.put("username", company.getUsername());
        userInfo.put("companyName", company.getCompanyName());
        userInfo.put("logo", company.getLogo());
        userInfo.put("contactName", company.getContactName());
        userInfo.put("contactPhone", company.getContactPhone());
        userInfo.put("contactEmail", company.getContactEmail());
        userInfo.put("industry", company.getIndustry());
        userInfo.put("scale", company.getScale());
        userInfo.put("nature", company.getNature());
        userInfo.put("address", company.getAddress());
        userInfo.put("introduction", company.getIntroduction());
        userInfo.put("auditStatus", company.getAuditStatus());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userInfo", userInfo);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProfile(Company company) {
        company.setPassword(null);
        company.setUsername(null);
        company.setAuditStatus(null);
        updateById(company);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void audit(Long id, Integer status, String remark) {
        Company company = getById(id);
        if (company == null) {
            throw BusinessException.of("企业不存在");
        }
        company.setAuditStatus(status);
        company.setAuditRemark(remark);
        updateById(company);
    }
}

