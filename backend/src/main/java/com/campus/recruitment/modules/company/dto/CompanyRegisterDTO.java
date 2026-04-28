package com.campus.recruitment.modules.company.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CompanyRegisterDTO {
    @NotBlank(message = "企业名称不能为空")
    private String companyName;
    @NotBlank(message = "用户名不能为空")
    private String username;
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, message = "密码不能少于6位")
    private String password;
    @NotBlank(message = "联系人不能为空")
    private String contactName;
    @NotBlank(message = "联系电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$|^\\d{3,4}-\\d{7,8}$", message = "联系电话格式不正确")
    private String contactPhone;
    @Email(message = "联系邮箱格式不正确")
    private String contactEmail;  // 允许为null/空，有值时校验格式
    private String industry;
    private String scale;
    private String nature;
    private String address;
    private String introduction;
}
