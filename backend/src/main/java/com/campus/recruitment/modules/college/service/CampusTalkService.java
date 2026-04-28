package com.campus.recruitment.modules.college.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.recruitment.modules.college.entity.CampusTalk;

public interface CampusTalkService extends IService<CampusTalk> {
    void publishTalk(CampusTalk campusTalk);
    void auditTalk(Long id, Integer status, String remark);
    Page<CampusTalk> pageTalks(Integer page, Integer size, Long collegeId, Integer status);
    /** 企业查自己的宣讲会（所有审核状态） */
    Page<CampusTalk> pageTalksByCompany(Integer page, Integer size, Long companyId);
    void signUp(Long talkId, Long studentId);
}
