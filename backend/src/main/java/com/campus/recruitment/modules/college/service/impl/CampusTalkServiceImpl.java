package com.campus.recruitment.modules.college.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.recruitment.common.exception.BusinessException;
import com.campus.recruitment.modules.college.entity.CampusTalk;
import com.campus.recruitment.modules.college.mapper.CampusTalkMapper;
import com.campus.recruitment.modules.college.service.CampusTalkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CampusTalkServiceImpl extends ServiceImpl<CampusTalkMapper, CampusTalk> implements CampusTalkService {

    @Override
    public void publishTalk(CampusTalk campusTalk) {
        campusTalk.setAuditStatus(0);
        campusTalk.setStatus(1);
        campusTalk.setCurrentSignNum(0);
        save(campusTalk);
    }

    @Override
    public void auditTalk(Long id, Integer status, String remark) {
        CampusTalk talk = getById(id);
        if (talk == null) {
            throw BusinessException.of("宣讲会不存在");
        }
        talk.setAuditStatus(status);
        talk.setAuditRemark(remark);
        updateById(talk);
    }

    @Override
    public Page<CampusTalk> pageTalks(Integer page, Integer size, Long collegeId, Integer status) {
        LambdaQueryWrapper<CampusTalk> wrapper = new LambdaQueryWrapper<CampusTalk>()
                .eq(CampusTalk::getAuditStatus, 1)
                .orderByAsc(CampusTalk::getTalkTime);
        if (collegeId != null) {
            wrapper.eq(CampusTalk::getCollegeId, collegeId);
        }
        if (status != null) {
            wrapper.eq(CampusTalk::getStatus, status);
        }
        return page(new Page<>(page, size), wrapper);
    }

    @Override
    public Page<CampusTalk> pageTalksByCompany(Integer page, Integer size, Long companyId) {
        return page(new Page<>(page, size),
                new LambdaQueryWrapper<CampusTalk>()
                        .eq(CampusTalk::getCompanyId, companyId)
                        .orderByDesc(CampusTalk::getCreateTime));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void signUp(Long talkId, Long studentId) {
        CampusTalk talk = getById(talkId);
        if (talk == null || talk.getStatus() != 1) {
            throw BusinessException.of("宣讲会不存在或已取消");
        }
        if (talk.getCurrentSignNum() >= talk.getMaxSignNum()) {
            throw BusinessException.of("报名人数已满");
        }
        talk.setCurrentSignNum(talk.getCurrentSignNum() + 1);
        // 使用乐观锁更新，如果版本号不匹配会抛出OptimisticLockerInnerInterceptor异常
        boolean success = updateById(talk);
        if (!success) {
            throw BusinessException.of("报名失败，请稍后重试");
        }
    }
}
