package com.campus.recruitment.modules.college.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.recruitment.common.exception.BusinessException;
import com.campus.recruitment.common.utils.JwtUtils;
import com.campus.recruitment.modules.college.entity.College;
import com.campus.recruitment.modules.college.mapper.CollegeMapper;
import com.campus.recruitment.modules.college.service.CollegeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CollegeServiceImpl extends ServiceImpl<CollegeMapper, College> implements CollegeService {

    private final JwtUtils jwtUtils;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public Map<String, Object> login(String username, String password) {
        College college = getOne(new LambdaQueryWrapper<College>()
                .eq(College::getUsername, username));

        if (college == null || !passwordEncoder.matches(password, college.getPassword())) {
            throw BusinessException.of("用户名或密码错误");
        }
        if (college.getStatus() == 0) {
            throw BusinessException.of("账号已被禁用");
        }
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", college.getId());
        claims.put("username", college.getUsername());
        claims.put("role", "COLLEGE");
        String token = jwtUtils.generateToken(claims);

        // 创建返回信息，隐藏密码
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", college.getId());
        userInfo.put("username", college.getUsername());
        userInfo.put("collegeName", college.getCollegeName());
        userInfo.put("collegeCode", college.getCollegeCode());
        userInfo.put("directorName", college.getDirectorName());
        userInfo.put("phone", college.getPhone());
        userInfo.put("email", college.getEmail());
        userInfo.put("introduction", college.getIntroduction());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userInfo", userInfo);
        return result;
    }
}

