package com.campus.recruitment.modules.job.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.recruitment.modules.job.entity.Application;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ApplicationMapper extends BaseMapper<Application> {

    /** 已就业学生总数（status=4，去重） */
    Long countEmployedStudents();

    /** 各学院学生总数统计 */
    List<Map<String, Object>> countStudentsByCollege();

    /** 各学院已就业学生数统计 */
    List<Map<String, Object>> countEmployedByCollege();

    /** 各专业学生总数统计 */
    List<Map<String, Object>> countStudentsByMajor();

    /** 各专业已就业学生数统计 */
    List<Map<String, Object>> countEmployedByMajor();

    /** 各行业就业分布统计 */
    List<Map<String, Object>> countEmployedByIndustry();

    /** 获取所有已就业学生ID列表 */
    List<Long> getEmployedStudentIds();
}

