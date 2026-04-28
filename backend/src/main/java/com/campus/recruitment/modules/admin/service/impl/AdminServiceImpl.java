package com.campus.recruitment.modules.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.recruitment.common.exception.BusinessException;
import com.campus.recruitment.common.utils.JwtUtils;
import com.campus.recruitment.modules.admin.entity.Admin;
import com.campus.recruitment.modules.admin.mapper.AdminMapper;
import com.campus.recruitment.modules.admin.service.AdminService;
import com.campus.recruitment.modules.college.entity.College;
import com.campus.recruitment.modules.company.service.CompanyService;
import com.campus.recruitment.modules.job.mapper.ApplicationMapper;
import com.campus.recruitment.modules.job.service.JobService;
import com.campus.recruitment.modules.student.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    private final JwtUtils jwtUtils;
    @Lazy
    private final StudentService studentService;
    @Lazy
    private final CompanyService companyService;
    @Lazy
    private final JobService jobService;
    private final ApplicationMapper applicationMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public Map<String, Object> login(String username, String password) {
        Admin admin = getOne(new LambdaQueryWrapper<Admin>()
                .eq(Admin::getUsername, username));

        if (admin == null || !passwordEncoder.matches(password, admin.getPassword())) {
            throw BusinessException.of("用户名或密码错误");
        }
        if (admin.getStatus() == 0) {
            throw BusinessException.of("账号已被禁用");
        }
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", admin.getId());
        claims.put("username", admin.getUsername());
        claims.put("role", "ADMIN");
        String token = jwtUtils.generateToken(claims);

        // 创建返回信息，隐藏密码
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", admin.getId());
        userInfo.put("username", admin.getUsername());
        userInfo.put("realName", admin.getRealName());
        userInfo.put("phone", admin.getPhone());
        userInfo.put("email", admin.getEmail());
        userInfo.put("avatar", admin.getAvatar());
        userInfo.put("isSuperAdmin", admin.getIsSuperAdmin());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userInfo", userInfo);
        return result;
    }

    @Override
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("studentCount", studentService.count());
        stats.put("companyCount", companyService.count());
        stats.put("jobCount", jobService.count());
        return stats;
    }

    @Override
    public Map<String, Object> getEmploymentStatistics() {
        Map<String, Object> result = new HashMap<>();

        // 1. 总学生数
        long totalStudents = studentService.count();
        result.put("totalStudents", totalStudents);

        // 2. 已就业学生数（status=4 已录用，去重）
        Long employedStudents = applicationMapper.countEmployedStudents();
        if (employedStudents == null) employedStudents = 0L;
        result.put("employedStudents", employedStudents);

        // 3. 就业率
        double employmentRate = totalStudents > 0 ? (employedStudents * 100.0 / totalStudents) : 0;
        result.put("employmentRate", Math.round(employmentRate * 100) / 100.0);

        // 4. 各学院就业统计
        List<Map<String, Object>> collegeStudentStats = applicationMapper.countStudentsByCollege();
        List<Map<String, Object>> collegeEmployedStats = applicationMapper.countEmployedByCollege();
        Map<Object, Long> employedByCollege = new HashMap<>();
        for (Map<String, Object> row : collegeEmployedStats) {
            employedByCollege.put(row.get("college_id"), ((Number) row.get("employed_count")).longValue());
        }
        List<Map<String, Object>> collegeStats = new ArrayList<>();
        for (Map<String, Object> row : collegeStudentStats) {
            Map<String, Object> stat = new HashMap<>();
            Long collegeId = ((Number) row.get("college_id")).longValue();
            stat.put("collegeId", collegeId);
            stat.put("collegeName", row.get("college_name"));
            Long total = ((Number) row.get("total_students")).longValue();
            stat.put("totalStudents", total);
            Long employed = employedByCollege.getOrDefault(collegeId, 0L);
            stat.put("employedStudents", employed);
            double rate = total > 0 ? (employed * 100.0 / total) : 0;
            stat.put("employmentRate", Math.round(rate * 100) / 100.0);
            collegeStats.add(stat);
        }
        result.put("collegeStats", collegeStats);

        // 5. 各专业就业统计
        List<Map<String, Object>> majorStudentStats = applicationMapper.countStudentsByMajor();
        List<Map<String, Object>> majorEmployedStats = applicationMapper.countEmployedByMajor();
        Map<Object, Long> employedByMajor = new HashMap<>();
        for (Map<String, Object> row : majorEmployedStats) {
            employedByMajor.put(row.get("major"), ((Number) row.get("employed_count")).longValue());
        }
        List<Map<String, Object>> majorStats = new ArrayList<>();
        for (Map<String, Object> row : majorStudentStats) {
            Map<String, Object> stat = new HashMap<>();
            String major = (String) row.get("major");
            stat.put("major", major);
            Long total = ((Number) row.get("total_students")).longValue();
            stat.put("totalStudents", total);
            Long employed = employedByMajor.getOrDefault(major, 0L);
            stat.put("employedStudents", employed);
            double rate = total > 0 ? (employed * 100.0 / total) : 0;
            stat.put("employmentRate", Math.round(rate * 100) / 100.0);
            majorStats.add(stat);
        }
        result.put("majorStats", majorStats);

        // 6. 各行业就业分布
        List<Map<String, Object>> industryStats = applicationMapper.countEmployedByIndustry();
        for (Map<String, Object> row : industryStats) {
            row.put("employedCount", ((Number) row.get("employed_count")).longValue());
            row.remove("employed_count");
        }
        result.put("industryStats", industryStats);

        return result;
    }

    @Override
    public Map<String, Object> getCollegeEmploymentStatistics(Long collegeId) {
        Map<String, Object> result = new HashMap<>();

        // 1. 该学院学生总数
        long totalStudents = studentService.count(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.campus.recruitment.modules.student.entity.Student>()
                        .eq(com.campus.recruitment.modules.student.entity.Student::getCollegeId, collegeId));
        result.put("totalStudents", totalStudents);

        // 2. 该学院已就业学生数（status=4）
        // 需要自定义查询：SELECT COUNT(DISTINCT student_id) FROM t_application a JOIN t_student s ON a.student_id = s.id WHERE a.status=4 AND s.college_id=?
        // 这里我直接查询所有已就业学生ID，然后在内存中过滤（学院数量不多，简单处理）
        List<Long> employedStudentIds = applicationMapper.getEmployedStudentIds();
        // 查询该学院所有学生ID
        List<Long> collegeStudentIds = studentService.list(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.campus.recruitment.modules.student.entity.Student>()
                        .eq(com.campus.recruitment.modules.student.entity.Student::getCollegeId, collegeId))
                .stream()
                .map(com.campus.recruitment.modules.student.entity.Student::getId)
                .collect(java.util.stream.Collectors.toList());

        long employedStudents = employedStudentIds.stream()
                .filter(collegeStudentIds::contains)
                .count();
        result.put("employedStudents", employedStudents);

        // 3. 就业率
        double employmentRate = totalStudents > 0 ? (employedStudents * 100.0 / totalStudents) : 0;
        result.put("employmentRate", Math.round(employmentRate * 100) / 100.0);

        // 4. 该学院各专业就业统计
        // 获取该学院所有学生
        List<com.campus.recruitment.modules.student.entity.Student> collegeStudents = studentService.list(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.campus.recruitment.modules.student.entity.Student>()
                        .eq(com.campus.recruitment.modules.student.entity.Student::getCollegeId, collegeId));
        Map<String, Long> majorStudentCounts = new HashMap<>();
        Map<String, Long> majorEmployedCounts = new HashMap<>();
        for (com.campus.recruitment.modules.student.entity.Student s : collegeStudents) {
            String major = s.getMajor() != null ? s.getMajor() : "未设置";
            majorStudentCounts.put(major, majorStudentCounts.getOrDefault(major, 0L) + 1);
            if (employedStudentIds.contains(s.getId())) {
                majorEmployedCounts.put(major, majorEmployedCounts.getOrDefault(major, 0L) + 1);
            }
        }
        List<Map<String, Object>> majorStats = new ArrayList<>();
        for (Map.Entry<String, Long> entry : majorStudentCounts.entrySet()) {
            Map<String, Object> stat = new HashMap<>();
            stat.put("major", entry.getKey());
            stat.put("totalStudents", entry.getValue());
            Long employed = majorEmployedCounts.getOrDefault(entry.getKey(), 0L);
            stat.put("employedStudents", employed);
            double rate = entry.getValue() > 0 ? (employed * 100.0 / entry.getValue()) : 0;
            stat.put("employmentRate", Math.round(rate * 100) / 100.0);
            majorStats.add(stat);
        }
        result.put("majorStats", majorStats);

        return result;
    }
}

