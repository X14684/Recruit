package com.campus.recruitment.modules.student.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.recruitment.common.exception.BusinessException;
import com.campus.recruitment.common.utils.JwtUtils;
import com.campus.recruitment.modules.student.dto.StudentRegisterDTO;
import com.campus.recruitment.modules.student.entity.Student;
import com.campus.recruitment.modules.student.mapper.StudentMapper;
import com.campus.recruitment.modules.student.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {

    private final JwtUtils jwtUtils;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(StudentRegisterDTO dto) {
        // 检查用户名是否重复
        long count = count(new LambdaQueryWrapper<Student>()
                .eq(Student::getUsername, dto.getUsername()));
        if (count > 0) {
            throw BusinessException.of("用户名已存在");
        }
        // 检查学号是否重复
        long count2 = count(new LambdaQueryWrapper<Student>()
                .eq(Student::getStudentNo, dto.getStudentNo()));
        if (count2 > 0) {
            throw BusinessException.of("学号已注册");
        }
        Student student = new Student();
        BeanUtils.copyProperties(dto, student);
        student.setPassword(passwordEncoder.encode(dto.getPassword()));
        student.setStatus(1);
        save(student);
    }

    @Override
    public Map<String, Object> login(String username, String password) {
        Student student = getOne(new LambdaQueryWrapper<Student>()
                .eq(Student::getUsername, username));

        if (student == null || !passwordEncoder.matches(password, student.getPassword())) {
            throw BusinessException.of("用户名或密码错误");
        }
        if (student.getStatus() == 0) {
            throw BusinessException.of("账号已被禁用，请联系管理员");
        }
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", student.getId());
        claims.put("username", student.getUsername());
        claims.put("role", "STUDENT");
        String token = jwtUtils.generateToken(claims);

        // 创建返回信息，隐藏密码
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", student.getId());
        userInfo.put("username", student.getUsername());
        userInfo.put("studentNo", student.getStudentNo());
        userInfo.put("realName", student.getRealName());
        userInfo.put("email", student.getEmail());
        userInfo.put("phone", student.getPhone());
        userInfo.put("gender", student.getGender());
        userInfo.put("avatar", student.getAvatar());
        userInfo.put("collegeId", student.getCollegeId());
        userInfo.put("major", student.getMajor());
        userInfo.put("grade", student.getGrade());
        userInfo.put("education", student.getEducation());
        userInfo.put("introduction", student.getIntroduction());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userInfo", userInfo);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProfile(Student student) {
        // 不允许修改密码和用户名
        student.setPassword(null);
        student.setUsername(null);
        updateById(student);
    }
}

