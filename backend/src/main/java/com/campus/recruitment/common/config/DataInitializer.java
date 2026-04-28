package com.campus.recruitment.common.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.recruitment.modules.admin.entity.Admin;
import com.campus.recruitment.modules.admin.mapper.AdminMapper;
import com.campus.recruitment.modules.college.entity.College;
import com.campus.recruitment.modules.college.mapper.CollegeMapper;
import com.campus.recruitment.modules.company.entity.Company;
import com.campus.recruitment.modules.company.mapper.CompanyMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 数据初始化器 - 启动时检查并修复数据
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final AdminMapper adminMapper;
    private final CompanyMapper companyMapper;
    private final CollegeMapper collegeMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        fixAdminPassword();
        fixCompanyPasswords();
        fixCollegePasswords();
    }

    private void fixAdminPassword() {
        try {
            Admin admin = adminMapper.selectById(1L);
            if (admin != null) {
                String testPassword = "admin123";
                String currentHash = admin.getPassword();
                
                if (!passwordEncoder.matches(testPassword, currentHash)) {
                    String newHash = passwordEncoder.encode(testPassword);
                    admin.setPassword(newHash);
                    adminMapper.updateById(admin);
                    log.info("admin password reset to: {}", testPassword);
                } else {
                    log.info("admin password verified, password: {}", testPassword);
                }
            }
        } catch (Exception e) {
            log.error("fix admin password failed", e);
        }
    }

    private void fixCompanyPasswords() {
        try {
            // 修复所有使用 Hutool BCrypt ($2b$) 的企业密码
            LambdaQueryWrapper<Company> wrapper = new LambdaQueryWrapper<Company>()
                    .like(Company::getPassword, "$2b$");
            var companies = companyMapper.selectList(wrapper);

            if (!companies.isEmpty()) {
                log.info("Found {} companies with Hutool BCrypt password, fixing...", companies.size());
                for (Company company : companies) {
                    // Hutool $2b$ 密码统一重置为 111111
                    String newHash = passwordEncoder.encode("111111");
                    company.setPassword(newHash);
                    companyMapper.updateById(company);
                    log.info("Company [{}] password fixed (Hutool -> Spring BCrypt)", company.getUsername());
                }
            } else {
                // 验证测试账号 123
                Company company = companyMapper.selectOne(
                    new LambdaQueryWrapper<Company>().eq(Company::getUsername, "123")
                );
                if (company != null && !passwordEncoder.matches("111111", company.getPassword())) {
                    company.setPassword(passwordEncoder.encode("111111"));
                    companyMapper.updateById(company);
                    log.info("company(123) password reset to: 111111");
                }
            }
        } catch (Exception e) {
            log.error("fix company password failed", e);
        }
    }

    private void fixCollegePasswords() {
        try {
            // 查找所有学院
            var colleges = collegeMapper.selectList(null);
            int fixedCount = 0;
            for (College college : colleges) {
                String currentHash = college.getPassword();
                // 密码为空，或不是 Spring BCrypt ($2a$ 开头)，或不是 123456 的哈希
                boolean needReset = false;
                if (currentHash == null || currentHash.trim().isEmpty()) {
                    needReset = true;
                } else if (!currentHash.startsWith("$2a$")) {
                    // Hutool BCrypt ($2b$) 或其他格式，需要重置
                    needReset = true;
                } else if (!passwordEncoder.matches("123456", currentHash)) {
                    // 哈希格式正确，但密码不是 123456
                    needReset = true;
                }

                if (needReset) {
                    String newHash = passwordEncoder.encode("123456");
                    college.setPassword(newHash);
                    collegeMapper.updateById(college);
                    fixedCount++;
                    log.info("College [{}] password reset to: 123456", college.getUsername());
                }
            }
            if (fixedCount > 0) {
                log.info("Fixed {} college passwords to default (123456)", fixedCount);
            } else {
                log.info("All college passwords verified (default: 123456)");
            }
        } catch (Exception e) {
            log.error("fix college passwords failed", e);
        }
    }
}
