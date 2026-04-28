package com.campus.recruitment.modules.student.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.recruitment.modules.student.dto.StudentRegisterDTO;
import com.campus.recruitment.modules.student.entity.Student;

import java.util.Map;

public interface StudentService extends IService<Student> {
    /** 学生注册 */
    void register(StudentRegisterDTO dto);
    /** 登录，返回token */
    Map<String, Object> login(String username, String password);
    /** 更新个人信息 */
    void updateProfile(Student student);
}
