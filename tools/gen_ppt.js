/**
 * 校园求职招聘系统 - 毕业答辩PPT生成脚本
 * 基于 PptxGenJS
 */
const pptxgen = require("pptxgenjs");
const path = require("path");

const pres = new pptxgen();
pres.layout = "LAYOUT_16x9";
pres.title = "基于Spring Boot的校园求职招聘系统的设计与实现";
pres.author = "许兴凯";

// ===== 颜色方案 =====
const C = {
  primary:   "1A3A5C",   // 深海军蓝 - 主色
  secondary: "2E7DB5",   // 天蓝 - 辅色
  accent:    "F5A623",   // 金黄 - 强调
  light:     "EBF4FB",   // 浅蓝背景
  white:     "FFFFFF",
  dark:      "1C2B3A",
  gray:      "6B7C93",
  lightGray: "F0F4F8",
  cardBg:    "FFFFFF",
  green:     "27AE60",
  orange:    "E67E22",
  purple:    "8E44AD",
  red:       "E74C3C",
};

// ===== 辅助函数 =====
function makeShadow() {
  return { type: "outer", color: "000000", blur: 8, offset: 3, angle: 135, opacity: 0.12 };
}

// 顶部装饰条
function addTopBar(slide, color) {
  slide.addShape(pres.shapes.RECTANGLE, {
    x: 0, y: 0, w: 10, h: 0.07,
    fill: { color: color || C.accent }, line: { color: color || C.accent }
  });
}

// 侧边装饰条（左侧标题旁）
function addSideAccent(slide, x, y, h, color) {
  slide.addShape(pres.shapes.RECTANGLE, {
    x, y, w: 0.06, h,
    fill: { color: color || C.accent }, line: { color: color || C.accent }
  });
}

// 统一内容页标题
function addSlideTitle(slide, title, subtitle) {
  // 顶部深色背景条
  slide.addShape(pres.shapes.RECTANGLE, {
    x: 0, y: 0, w: 10, h: 1.1,
    fill: { color: C.primary }, line: { color: C.primary }
  });
  // 左侧金色强调线
  slide.addShape(pres.shapes.RECTANGLE, {
    x: 0.35, y: 0.22, w: 0.06, h: 0.66,
    fill: { color: C.accent }, line: { color: C.accent }
  });
  slide.addText(title, {
    x: 0.55, y: 0.2, w: 8.5, h: 0.7,
    fontSize: 22, bold: true, color: C.white, fontFace: "Microsoft YaHei",
    valign: "middle", margin: 0
  });
  if (subtitle) {
    slide.addText(subtitle, {
      x: 0.55, y: 0.82, w: 8.5, h: 0.28,
      fontSize: 11, color: "AECDE8", fontFace: "Microsoft YaHei",
      valign: "middle", margin: 0
    });
  }
}

// 卡片背景
function addCard(slide, x, y, w, h, color) {
  slide.addShape(pres.shapes.RECTANGLE, {
    x, y, w, h,
    fill: { color: color || C.cardBg },
    line: { color: "D8E5F0", width: 0.5 },
    shadow: makeShadow()
  });
}

// ================================================================
// Slide 01 - 封面
// ================================================================
{
  const slide = pres.addSlide();

  // 深色主背景
  slide.background = { color: C.primary };

  // 右侧装饰几何图形
  slide.addShape(pres.shapes.RECTANGLE, {
    x: 6.8, y: 0, w: 3.2, h: 5.625,
    fill: { color: "152E4A" }, line: { color: "152E4A" }
  });
  // 对角斜线装饰
  slide.addShape(pres.shapes.RECTANGLE, {
    x: 6.2, y: 0, w: 0.7, h: 5.625,
    fill: { color: "1E4068", transparency: 40 }, line: { color: "1E4068", transparency: 40 }
  });

  // 金色顶部装饰线
  slide.addShape(pres.shapes.RECTANGLE, {
    x: 0, y: 0, w: 6.6, h: 0.08,
    fill: { color: C.accent }, line: { color: C.accent }
  });

  // 右侧图标区域 - 电脑图案（用矩形组合）
  slide.addShape(pres.shapes.RECTANGLE, {
    x: 7.3, y: 1.0, w: 2.0, h: 1.4,
    fill: { color: "203A56" }, line: { color: C.secondary, width: 1.5 },
    rectRadius: 0.05
  });
  slide.addShape(pres.shapes.RECTANGLE, {
    x: 7.5, y: 1.15, w: 1.6, h: 1.0,
    fill: { color: "0D2137" }, line: { color: C.secondary, width: 0.5 }
  });
  // 屏幕内容模拟
  slide.addText("< / >", {
    x: 7.5, y: 1.35, w: 1.6, h: 0.6,
    fontSize: 18, bold: true, color: C.secondary,
    align: "center", fontFace: "Consolas"
  });
  // 键盘底座
  slide.addShape(pres.shapes.RECTANGLE, {
    x: 7.65, y: 2.4, w: 1.3, h: 0.12,
    fill: { color: "203A56" }, line: { color: C.secondary, width: 1 }
  });
  slide.addShape(pres.shapes.RECTANGLE, {
    x: 7.2, y: 2.52, w: 2.2, h: 0.22,
    fill: { color: "203A56" }, line: { color: C.secondary, width: 1 }
  });

  // 右侧圆形装饰
  slide.addShape(pres.shapes.OVAL, {
    x: 7.0, y: 3.1, w: 2.6, h: 2.6,
    fill: { color: C.secondary, transparency: 85 }, line: { color: C.secondary, transparency: 70 }
  });
  slide.addShape(pres.shapes.OVAL, {
    x: 7.4, y: 3.5, w: 1.8, h: 1.8,
    fill: { color: C.accent, transparency: 90 }, line: { color: C.accent, transparency: 80 }
  });

  // 校名
  slide.addText("潍坊理工学院", {
    x: 0.5, y: 0.55, w: 6.0, h: 0.5,
    fontSize: 14, color: "AECDE8", fontFace: "Microsoft YaHei",
    bold: false
  });

  // 主标题
  slide.addText("基于 Spring Boot 的", {
    x: 0.5, y: 1.1, w: 6.0, h: 0.65,
    fontSize: 28, bold: true, color: C.white, fontFace: "Microsoft YaHei"
  });
  slide.addText("校园求职招聘系统", {
    x: 0.5, y: 1.72, w: 6.0, h: 0.75,
    fontSize: 32, bold: true, color: C.accent, fontFace: "Microsoft YaHei"
  });
  slide.addText("设计与实现", {
    x: 0.5, y: 2.44, w: 6.0, h: 0.6,
    fontSize: 26, bold: true, color: C.white, fontFace: "Microsoft YaHei"
  });

  // 分隔线
  slide.addShape(pres.shapes.LINE, {
    x: 0.5, y: 3.2, w: 5.0, h: 0,
    line: { color: C.accent, width: 1.5 }
  });

  // 信息栏
  const info = [
    ["专业", "计算机科学与技术"],
    ["学生", "许兴凯  202202110111"],
    ["指导教师", "王敏  讲师"],
    ["答辩时间", "2026年5月"],
  ];
  info.forEach(([label, val], i) => {
    slide.addText(label + "：", {
      x: 0.5, y: 3.35 + i * 0.42, w: 1.2, h: 0.38,
      fontSize: 11, color: "AECDE8", fontFace: "Microsoft YaHei",
      bold: true, valign: "middle", margin: 0
    });
    slide.addText(val, {
      x: 1.72, y: 3.35 + i * 0.42, w: 3.8, h: 0.38,
      fontSize: 11, color: C.white, fontFace: "Microsoft YaHei",
      valign: "middle", margin: 0
    });
  });
}

// ================================================================
// Slide 02 - 目录
// ================================================================
{
  const slide = pres.addSlide();
  slide.background = { color: C.lightGray };
  addTopBar(slide, C.primary);

  // 左侧深色背景
  slide.addShape(pres.shapes.RECTANGLE, {
    x: 0, y: 0.07, w: 3.2, h: 5.555,
    fill: { color: C.primary }, line: { color: C.primary }
  });

  slide.addText("汇报目录", {
    x: 0.2, y: 0.5, w: 2.8, h: 0.7,
    fontSize: 22, bold: true, color: C.white, fontFace: "Microsoft YaHei",
    align: "center"
  });
  slide.addText("CONTENTS", {
    x: 0.2, y: 1.15, w: 2.8, h: 0.35,
    fontSize: 11, color: "AECDE8", fontFace: "Arial",
    align: "center", charSpacing: 3
  });

  // 装饰圆
  slide.addShape(pres.shapes.OVAL, {
    x: 0.4, y: 4.3, w: 2.4, h: 2.4,
    fill: { color: C.secondary, transparency: 80 }, line: { color: C.secondary, transparency: 70 }
  });

  const items = [
    { num: "01", text: "项目背景与选题意义" },
    { num: "02", text: "系统整体设计与技术路线" },
    { num: "03", text: "系统功能模块详解" },
    { num: "04", text: "核心业务流程与算法" },
    { num: "05", text: "代码实现逻辑展示" },
    { num: "06", text: "总结与展望" },
  ];

  items.forEach((item, i) => {
    const y = 0.75 + i * 0.8;
    // 卡片
    addCard(slide, 3.4, y, 6.3, 0.65, C.white);
    // 编号圆
    slide.addShape(pres.shapes.OVAL, {
      x: 3.55, y: y + 0.1, w: 0.45, h: 0.45,
      fill: { color: i === 0 ? C.accent : C.secondary }, line: { color: i === 0 ? C.accent : C.secondary }
    });
    slide.addText(item.num, {
      x: 3.55, y: y + 0.1, w: 0.45, h: 0.45,
      fontSize: 11, bold: true, color: C.white, align: "center", valign: "middle",
      fontFace: "Arial", margin: 0
    });
    slide.addText(item.text, {
      x: 4.1, y: y + 0.12, w: 5.4, h: 0.42,
      fontSize: 15, color: C.dark, fontFace: "Microsoft YaHei",
      valign: "middle", bold: i === 0, margin: 0
    });
  });
}

// ================================================================
// Slide 03 - 项目背景与选题意义
// ================================================================
{
  const slide = pres.addSlide();
  slide.background = { color: C.lightGray };
  addSlideTitle(slide, "01  项目背景与选题意义", "Background & Significance");

  // 左侧：问题卡片
  addCard(slide, 0.3, 1.25, 4.4, 3.9);

  slide.addShape(pres.shapes.RECTANGLE, {
    x: 0.3, y: 1.25, w: 4.4, h: 0.5,
    fill: { color: C.red }, line: { color: C.red }
  });
  slide.addText("传统校园招聘的痛点", {
    x: 0.3, y: 1.25, w: 4.4, h: 0.5,
    fontSize: 13, bold: true, color: C.white, fontFace: "Microsoft YaHei",
    align: "center", valign: "middle", margin: 0
  });

  const problems = [
    ["信息不对称", "学生难获取匹配职位，企业难触达目标人才"],
    ["效率低下", "线下双选会耗时耗力，流程繁琐"],
    ["时空限制", "双选会受场地时间限制，学生机会不均等"],
    ["技术陈旧", "传统系统前后端耦合，用户体验差"],
    ["智能化低", "缺乏个性化推荐，信息过载问题突出"],
  ];
  problems.forEach(([title, desc], i) => {
    const y = 1.88 + i * 0.52;
    slide.addShape(pres.shapes.OVAL, {
      x: 0.5, y: y + 0.07, w: 0.28, h: 0.28,
      fill: { color: C.red }, line: { color: C.red }
    });
    slide.addText(title, {
      x: 0.88, y, w: 1.5, h: 0.42,
      fontSize: 11, bold: true, color: C.dark, fontFace: "Microsoft YaHei",
      valign: "middle", margin: 0
    });
    slide.addText(desc, {
      x: 2.38, y, w: 2.15, h: 0.42,
      fontSize: 9.5, color: C.gray, fontFace: "Microsoft YaHei",
      valign: "middle", margin: 0
    });
  });

  // 右侧：意义卡片
  addCard(slide, 5.0, 1.25, 4.7, 3.9);
  slide.addShape(pres.shapes.RECTANGLE, {
    x: 5.0, y: 1.25, w: 4.7, h: 0.5,
    fill: { color: C.green }, line: { color: C.green }
  });
  slide.addText("系统建设的三重意义", {
    x: 5.0, y: 1.25, w: 4.7, h: 0.5,
    fontSize: 13, bold: true, color: C.white, fontFace: "Microsoft YaHei",
    align: "center", valign: "middle", margin: 0
  });

  const benefits = [
    { role: "学生", color: C.secondary, items: ["一站式求职门户", "快捷投递简历", "实时跟踪求职进度"] },
    { role: "企业", color: C.orange, items: ["精准对接目标人才", "降低招聘成本", "优化简历筛选流程"] },
    { role: "高校", color: C.green, items: ["数字化就业管理", "沉淀就业数据", "支持智慧校园建设"] },
  ];
  benefits.forEach((b, i) => {
    const y = 1.88 + i * 0.98;
    slide.addShape(pres.shapes.RECTANGLE, {
      x: 5.15, y: y + 0.06, w: 0.8, h: 0.32,
      fill: { color: b.color }, line: { color: b.color }
    });
    slide.addText(b.role, {
      x: 5.15, y: y + 0.06, w: 0.8, h: 0.32,
      fontSize: 11, bold: true, color: C.white, align: "center", valign: "middle",
      fontFace: "Microsoft YaHei", margin: 0
    });
    b.items.forEach((item, j) => {
      slide.addText("• " + item, {
        x: 6.1, y: y + j * 0.28, w: 3.4, h: 0.3,
        fontSize: 10, color: C.dark, fontFace: "Microsoft YaHei",
        valign: "middle", margin: 0
      });
    });
  });
}

// ================================================================
// Slide 04 - 系统整体设计与技术路线
// ================================================================
{
  const slide = pres.addSlide();
  slide.background = { color: C.lightGray };
  addSlideTitle(slide, "02  系统整体设计与技术路线", "System Architecture & Technology Stack");

  // 整体架构图（分三层）
  const layers = [
    { label: "前端展示层", color: C.secondary, items: "Vue 3 + Vite  |  Element Plus  |  Pinia  |  ECharts  |  Axios" },
    { label: "后端服务层", color: C.primary, items: "Spring Boot 3.5  |  Spring Security+JWT  |  MyBatis-Plus  |  RESTful API" },
    { label: "数据存储层", color: C.green, items: "MySQL  |  数据库索引优化  |  连接池配置" },
  ];

  layers.forEach((layer, i) => {
    const y = 1.2 + i * 1.12;
    slide.addShape(pres.shapes.RECTANGLE, {
      x: 0.3, y, w: 1.4, h: 0.78,
      fill: { color: layer.color }, line: { color: layer.color }
    });
    slide.addText(layer.label, {
      x: 0.3, y, w: 1.4, h: 0.78,
      fontSize: 11, bold: true, color: C.white, align: "center", valign: "middle",
      fontFace: "Microsoft YaHei", margin: 0
    });
    addCard(slide, 1.85, y, 7.85, 0.78);
    slide.addText(layer.items, {
      x: 1.95, y: y + 0.1, w: 7.6, h: 0.58,
      fontSize: 12, color: C.dark, fontFace: "Microsoft YaHei",
      valign: "middle", margin: 0
    });
  });

  // 箭头
  [1.98, 3.1].forEach(y => {
    slide.addShape(pres.shapes.LINE, {
      x: 0.85, y, w: 0, h: 0.32,
      line: { color: C.accent, width: 1.5 }
    });
  });

  // 底部技术选型说明
  addCard(slide, 0.3, 4.7, 9.4, 0.7);
  slide.addShape(pres.shapes.RECTANGLE, {
    x: 0.3, y: 4.7, w: 0.06, h: 0.7,
    fill: { color: C.accent }, line: { color: C.accent }
  });
  slide.addText("前后端分离 B/S 架构：前端 SPA 通过 HTTP + JSON 调用后端 RESTful API；JWT Token 无状态鉴权；CORS 跨域配置；Vite Dev Proxy 开发代理", {
    x: 0.5, y: 4.72, w: 9.1, h: 0.66,
    fontSize: 11, color: C.gray, fontFace: "Microsoft YaHei",
    valign: "middle", margin: 0
  });
}

// ================================================================
// Slide 05 - 系统功能模块总览
// ================================================================
{
  const slide = pres.addSlide();
  slide.background = { color: C.lightGray };
  addSlideTitle(slide, "03  系统功能模块总览", "System Function Modules");

  const modules = [
    {
      title: "学生端", color: C.secondary, icon: "👨‍🎓",
      items: ["注册登录 / JWT认证", "个人信息 & 求职意向管理", "在线简历创建与编辑", "职位搜索 & 筛选 & 收藏", "一键投递 & 进度跟踪", "接收面试邀请通知"]
    },
    {
      title: "企业端", color: C.orange, icon: "🏢",
      items: ["企业注册 & 认证材料上传", "企业资料管理", "发布职位 & 上下架管理", "简历筛选 & 投递管理", "发送面试邀请", "宣讲会发布与管理"]
    },
    {
      title: "学院端", color: C.green, icon: "🏫",
      items: ["学院账号管理", "审核企业注册与招聘信息", "维护学生信息", "发布就业公告", "宣讲会审核管理", "就业情况统计"]
    },
    {
      title: "管理员", color: C.primary, icon: "🔧",
      items: ["用户账号全局管理", "RBAC权限配置", "数据统计报表(ECharts)", "公告管理", "学院账号管理", "系统配置与字典"]
    },
  ];

  modules.forEach((mod, i) => {
    const x = 0.15 + i * 2.45;
    addCard(slide, x, 1.2, 2.3, 4.15);
    slide.addShape(pres.shapes.RECTANGLE, {
      x, y: 1.2, w: 2.3, h: 0.65,
      fill: { color: mod.color }, line: { color: mod.color }
    });
    slide.addText(mod.title, {
      x: x + 0.05, y: 1.2, w: 2.2, h: 0.65,
      fontSize: 14, bold: true, color: C.white, align: "center", valign: "middle",
      fontFace: "Microsoft YaHei", margin: 0
    });
    mod.items.forEach((item, j) => {
      // 交替行背景
      if (j % 2 === 0) {
        slide.addShape(pres.shapes.RECTANGLE, {
          x, y: 1.85 + j * 0.58, w: 2.3, h: 0.58,
          fill: { color: C.lightGray }, line: { color: C.lightGray }
        });
      }
      slide.addText(item, {
        x: x + 0.12, y: 1.88 + j * 0.58, w: 2.0, h: 0.52,
        fontSize: 10, color: C.dark, fontFace: "Microsoft YaHei",
        valign: "middle", margin: 0
      });
    });
  });
}

// ================================================================
// Slide 06 - 核心业务流程：求职投递
// ================================================================
{
  const slide = pres.addSlide();
  slide.background = { color: C.lightGray };
  addSlideTitle(slide, "04  核心业务流程 — 求职投递全流程", "Core Business Process: Job Application");

  // 流程步骤
  const steps = [
    { step: "01", title: "注册登录", desc: "学生注册账号\nJWT Token获取", color: C.secondary },
    { step: "02", title: "完善简历", desc: "填写个人信息\n求职意向设置", color: C.green },
    { step: "03", title: "职位搜索", desc: "关键词筛选\n分页浏览职位", color: C.orange },
    { step: "04", title: "一键投递", desc: "选择简历\n提交申请", color: C.accent },
    { step: "05", title: "企业审核", desc: "HR筛选简历\n更新申请状态", color: C.purple },
    { step: "06", title: "面试通知", desc: "系统通知学生\n确认面试时间", color: C.red },
  ];

  steps.forEach((s, i) => {
    const x = 0.2 + i * 1.6;
    const y = 1.3;
    // 圆形编号
    slide.addShape(pres.shapes.OVAL, {
      x: x + 0.3, y, w: 0.9, h: 0.9,
      fill: { color: s.color }, line: { color: s.color }
    });
    slide.addText(s.step, {
      x: x + 0.3, y, w: 0.9, h: 0.9,
      fontSize: 14, bold: true, color: C.white, align: "center", valign: "middle",
      fontFace: "Arial", margin: 0
    });
    // 竖线连接
    if (i < steps.length - 1) {
      slide.addShape(pres.shapes.LINE, {
        x: x + 1.25, y: y + 0.45, w: 0.6, h: 0,
        line: { color: C.gray, width: 1.5, dashType: "dash" }
      });
    }
    // 标题
    slide.addText(s.title, {
      x: x + 0.1, y: y + 1.0, w: 1.3, h: 0.35,
      fontSize: 12, bold: true, color: s.color, align: "center",
      fontFace: "Microsoft YaHei", margin: 0
    });
    // 描述
    slide.addText(s.desc, {
      x: x + 0.05, y: y + 1.35, w: 1.4, h: 0.5,
      fontSize: 9.5, color: C.gray, align: "center",
      fontFace: "Microsoft YaHei", margin: 0
    });
  });

  // 企业侧审核流程（下方补充）
  addCard(slide, 0.3, 3.2, 9.4, 2.15);
  slide.addShape(pres.shapes.RECTANGLE, {
    x: 0.3, y: 3.2, w: 0.06, h: 2.15,
    fill: { color: C.primary }, line: { color: C.primary }
  });
  slide.addText("企业端关键操作", {
    x: 0.5, y: 3.25, w: 2.2, h: 0.4,
    fontSize: 12, bold: true, color: C.primary, fontFace: "Microsoft YaHei",
    valign: "middle", margin: 0
  });

  const enterpriseOps = [
    ["发布职位", "填写岗位名称、薪资、要求，提交学院/管理员审核"],
    ["宣讲会管理", "申请宣讲会时间地点，审核通过后学生可报名"],
    ["简历筛选", "查看投递列表，筛选候选人，更新投递状态（通过/拒绝/面试）"],
    ["面试安排", "设置面试时间，系统自动推送通知给学生"],
  ];
  enterpriseOps.forEach(([op, desc], i) => {
    const x = 0.5 + (i % 2) * 4.6;
    const y = 3.7 + Math.floor(i / 2) * 0.62;
    slide.addShape(pres.shapes.RECTANGLE, {
      x, y: y + 0.05, w: 0.9, h: 0.32,
      fill: { color: C.orange }, line: { color: C.orange }
    });
    slide.addText(op, {
      x, y: y + 0.05, w: 0.9, h: 0.32,
      fontSize: 10, bold: true, color: C.white, align: "center", valign: "middle",
      fontFace: "Microsoft YaHei", margin: 0
    });
    slide.addText(desc, {
      x: x + 1.0, y, w: 3.4, h: 0.44,
      fontSize: 10, color: C.gray, fontFace: "Microsoft YaHei",
      valign: "middle", margin: 0
    });
  });
}

// ================================================================
// Slide 07 - 数据库设计
// ================================================================
{
  const slide = pres.addSlide();
  slide.background = { color: C.lightGray };
  addSlideTitle(slide, "04  数据库设计", "Database Design");

  const tables = [
    { name: "user", label: "用户表", color: C.secondary, fields: ["id, username, password", "role(student/company/college/admin)", "createTime, status"] },
    { name: "student_profile", label: "学生信息表", color: C.green, fields: ["userId(FK)", "name, studentNo, major", "resumeUrl, intention"] },
    { name: "company", label: "企业信息表", color: C.orange, fields: ["userId(FK)", "companyName, industry", "auditStatus, description"] },
    { name: "job", label: "职位表", color: C.purple, fields: ["companyId(FK)", "title, salary, location", "requirements, auditStatus"] },
    { name: "application", label: "投递记录表", color: C.red, fields: ["studentId(FK), jobId(FK)", "status(待处理/通过/拒绝/面试)", "applyTime, interviewTime"] },
    { name: "campus_talk", label: "宣讲会表", color: C.primary, fields: ["companyId(FK)", "title, venue, startTime", "auditStatus, signUpCount"] },
  ];

  tables.forEach((t, i) => {
    const col = i % 3;
    const row = Math.floor(i / 3);
    const x = 0.2 + col * 3.25;
    const y = 1.2 + row * 2.05;
    addCard(slide, x, y, 3.05, 1.9);
    slide.addShape(pres.shapes.RECTANGLE, {
      x, y, w: 3.05, h: 0.45,
      fill: { color: t.color }, line: { color: t.color }
    });
    slide.addText(t.label + "  [" + t.name + "]", {
      x, y, w: 3.05, h: 0.45,
      fontSize: 11, bold: true, color: C.white, align: "center", valign: "middle",
      fontFace: "Microsoft YaHei", margin: 0
    });
    t.fields.forEach((f, fi) => {
      slide.addText(f, {
        x: x + 0.15, y: y + 0.52 + fi * 0.42, w: 2.75, h: 0.38,
        fontSize: 10, color: C.dark, fontFace: "Consolas",
        valign: "middle", margin: 0
      });
    });
  });
}

// ================================================================
// Slide 08 - 核心代码实现逻辑 - 认证
// ================================================================
{
  const slide = pres.addSlide();
  slide.background = { color: C.lightGray };
  addSlideTitle(slide, "05  核心代码实现 — 安全认证体系", "JWT + Spring Security Authentication");

  // 左：流程说明
  addCard(slide, 0.3, 1.2, 4.2, 4.2);
  slide.addShape(pres.shapes.RECTANGLE, {
    x: 0.3, y: 1.2, w: 4.2, h: 0.45,
    fill: { color: C.secondary }, line: { color: C.secondary }
  });
  slide.addText("JWT 认证流程", {
    x: 0.3, y: 1.2, w: 4.2, h: 0.45,
    fontSize: 13, bold: true, color: C.white, align: "center", valign: "middle",
    fontFace: "Microsoft YaHei", margin: 0
  });

  const authFlow = [
    { step: "1", text: "用户携带 username/password 请求 /user/login", color: C.secondary },
    { step: "2", text: "Spring Security 验证密码（BCryptPasswordEncoder）", color: C.secondary },
    { step: "3", text: "验证通过后 JwtUtils 生成 Token（包含 userId、role）", color: C.green },
    { step: "4", text: "前端将 Token 存入 localStorage，每次请求放入 Header", color: C.green },
    { step: "5", text: "JwtAuthFilter 拦截请求，解析 Token 并设置 SecurityContext", color: C.orange },
    { step: "6", text: "白名单接口（登录/注册/公开列表）无需 Token 直接放行", color: C.orange },
  ];

  authFlow.forEach((a, i) => {
    slide.addShape(pres.shapes.OVAL, {
      x: 0.45, y: 1.78 + i * 0.56, w: 0.3, h: 0.3,
      fill: { color: a.color }, line: { color: a.color }
    });
    slide.addText(a.step, {
      x: 0.45, y: 1.78 + i * 0.56, w: 0.3, h: 0.3,
      fontSize: 9, bold: true, color: C.white, align: "center", valign: "middle",
      fontFace: "Arial", margin: 0
    });
    slide.addText(a.text, {
      x: 0.85, y: 1.74 + i * 0.56, w: 3.55, h: 0.5,
      fontSize: 10, color: C.dark, fontFace: "Microsoft YaHei",
      valign: "middle", margin: 0
    });
  });

  // 右：关键代码片段
  addCard(slide, 4.7, 1.2, 5.0, 4.2, "1E2D3D");

  slide.addText("// JwtAuthFilter.java 核心逻辑", {
    x: 4.85, y: 1.3, w: 4.7, h: 0.3,
    fontSize: 9.5, color: "6A9955", fontFace: "Consolas", margin: 0
  });
  const codeLines = [
    { text: "String token = request", color: "D4D4D4" },
    { text: "  .getHeader(\"Authorization\");", color: "D4D4D4" },
    { text: "if (token != null &&", color: "D4D4D4" },
    { text: "    token.startsWith(\"Bearer \")) {", color: "D4D4D4" },
    { text: "  Claims claims =", color: "D4D4D4" },
    { text: "    jwtUtils.parseToken(token);", color: "4EC9B0" },
    { text: "  Long userId = claims", color: "D4D4D4" },
    { text: "    .get(\"userId\", Long.class);", color: "9CDCFE" },
    { text: "  // 设置认证上下文", color: "6A9955" },
    { text: "  SecurityContextHolder", color: "D4D4D4" },
    { text: "    .getContext()", color: "D4D4D4" },
    { text: "    .setAuthentication(auth);", color: "4EC9B0" },
    { text: "}", color: "D4D4D4" },
  ];
  codeLines.forEach((line, i) => {
    slide.addText(line.text, {
      x: 4.85, y: 1.65 + i * 0.27, w: 4.7, h: 0.28,
      fontSize: 9.5, color: line.color, fontFace: "Consolas",
      valign: "middle", margin: 0
    });
  });

  // RBAC 说明
  addCard(slide, 4.7, 5.1, 5.0, 0.32);
  slide.addText("RBAC 多角色：student / company / college / admin  →  @PreAuthorize 方法级鉴权", {
    x: 4.85, y: 5.1, w: 4.7, h: 0.32,
    fontSize: 9.5, color: C.gray, fontFace: "Microsoft YaHei",
    valign: "middle", margin: 0
  });
}

// ================================================================
// Slide 09 - 核心代码实现逻辑 - 分页查询
// ================================================================
{
  const slide = pres.addSlide();
  slide.background = { color: C.lightGray };
  addSlideTitle(slide, "05  核心代码实现 — 职位分页查询与投递管理", "Job Paging & Application Management");

  // 左侧：分页查询
  addCard(slide, 0.3, 1.2, 4.5, 4.2, "1E2D3D");
  slide.addShape(pres.shapes.RECTANGLE, {
    x: 0.3, y: 1.2, w: 4.5, h: 0.45,
    fill: { color: C.primary }, line: { color: C.primary }
  });
  slide.addText("职位分页查询 (JobServiceImpl)", {
    x: 0.3, y: 1.2, w: 4.5, h: 0.45,
    fontSize: 12, bold: true, color: C.white, align: "center", valign: "middle",
    fontFace: "Microsoft YaHei", margin: 0
  });

  const jobCode = [
    "// 企业查自己职位不过滤状态",
    "LambdaQueryWrapper<Job> wrapper",
    "  = new LambdaQueryWrapper<>();",
    "if (companyId != null) {",
    "  wrapper.eq(Job::getCompanyId,",
    "             companyId);",
    "} else {",
    "  // 公开查询只返回已审核",
    "  wrapper.eq(Job::getAuditStatus,1)",
    "         .eq(Job::getStatus, 1);",
    "}",
    "// MyBatis-Plus 分页",
    "Page<Job> page = jobMapper",
    "  .selectPage(pageParam, wrapper);",
  ];
  jobCode.forEach((line, i) => {
    slide.addText(line, {
      x: 0.45, y: 1.72 + i * 0.27, w: 4.2, h: 0.28,
      fontSize: 9.5,
      color: line.startsWith("//") ? "6A9955" : (line.includes("if") || line.includes("else") || line.includes("}") ? "C586C0" : "D4D4D4"),
      fontFace: "Consolas",
      valign: "middle", margin: 0
    });
  });

  // 右侧：分页陷阱 + 投递状态机
  addCard(slide, 5.0, 1.2, 4.7, 2.0);
  slide.addShape(pres.shapes.RECTANGLE, {
    x: 5.0, y: 1.2, w: 4.7, h: 0.45,
    fill: { color: C.red }, line: { color: C.red }
  });
  slide.addText("⚠ MyBatis-Plus 分页陷阱", {
    x: 5.0, y: 1.2, w: 4.7, h: 0.45,
    fontSize: 12, bold: true, color: C.white, align: "center", valign: "middle",
    fontFace: "Microsoft YaHei", margin: 0
  });

  const trapText = [
    "new Page<>(current, size, total)",
    "  .getRecords()  // 不可变空列表!",
    "",
    "// ✅ 修复方案：",
    "voPage.setRecords(new ArrayList<>());",
    "voPage.getRecords().add(vo);",
  ];
  trapText.forEach((line, i) => {
    slide.addText(line, {
      x: 5.15, y: 1.72 + i * 0.27, w: 4.4, h: 0.28,
      fontSize: 9.5,
      color: line.startsWith("//") || line.startsWith("// ✅") ? "6A9955" : (line.includes("修复") ? "4EC9B0" : "D4D4D4"),
      fontFace: "Consolas",
      valign: "middle", margin: 0
    });
  });

  // 投递状态机
  addCard(slide, 5.0, 3.3, 4.7, 2.1);
  slide.addShape(pres.shapes.RECTANGLE, {
    x: 5.0, y: 3.3, w: 4.7, h: 0.45,
    fill: { color: C.secondary }, line: { color: C.secondary }
  });
  slide.addText("投递状态流转", {
    x: 5.0, y: 3.3, w: 4.7, h: 0.45,
    fontSize: 12, bold: true, color: C.white, align: "center", valign: "middle",
    fontFace: "Microsoft YaHei", margin: 0
  });

  const statuses = [
    { text: "待处理", color: C.gray },
    { text: "→", color: C.gray },
    { text: "通过", color: C.green },
    { text: "→", color: C.gray },
    { text: "面试", color: C.orange },
  ];
  const statusRow2 = [
    { text: "待处理", color: C.gray },
    { text: "→", color: C.gray },
    { text: "拒绝", color: C.red },
  ];

  statuses.forEach((s, i) => {
    slide.addText(s.text, {
      x: 5.2 + i * 0.8, y: 3.85, w: 0.7, h: 0.4,
      fontSize: 11, bold: true, color: s.color, align: "center",
      fontFace: "Microsoft YaHei", margin: 0
    });
  });
  statusRow2.forEach((s, i) => {
    slide.addText(s.text, {
      x: 5.2 + i * 0.8, y: 4.3, w: 0.7, h: 0.4,
      fontSize: 11, bold: true, color: s.color, align: "center",
      fontFace: "Microsoft YaHei", margin: 0
    });
  });

  slide.addText("面试通知支持 ISO8601（带T）和传统格式（带空格）双格式解析", {
    x: 5.15, y: 4.8, w: 4.5, h: 0.45,
    fontSize: 9.5, color: C.gray, fontFace: "Microsoft YaHei",
    valign: "middle", margin: 0
  });
}

// ================================================================
// Slide 10 - 系统界面展示（功能演示）
// ================================================================
{
  const slide = pres.addSlide();
  slide.background = { color: C.lightGray };
  addSlideTitle(slide, "05  系统功能界面展示", "System Interface Demo");

  const screens = [
    { title: "首页 / 职位列表", color: C.secondary, desc: "职位搜索、分页浏览\n关键词 + 类型筛选" },
    { title: "个人简历管理", color: C.green, desc: "在线填写基本信息\n工作/教育经历录入" },
    { title: "企业职位管理", color: C.orange, desc: "发布/编辑职位\n投递列表管理" },
    { title: "管理员后台", color: C.primary, desc: "数据统计 ECharts\n用户/学院管理" },
    { title: "宣讲会管理", color: C.purple, desc: "企业申请宣讲\n学院审核发布" },
    { title: "投递进度跟踪", color: C.red, desc: "实时查看申请状态\n面试时间通知" },
  ];

  screens.forEach((s, i) => {
    const col = i % 3;
    const row = Math.floor(i / 3);
    const x = 0.2 + col * 3.2;
    const y = 1.2 + row * 2.1;
    addCard(slide, x, y, 3.0, 1.95);

    // 模拟屏幕区域
    slide.addShape(pres.shapes.RECTANGLE, {
      x: x + 0.12, y: y + 0.12, w: 2.76, h: 1.1,
      fill: { color: s.color, transparency: 88 }, line: { color: s.color, width: 0.5 }
    });
    // 模拟标题栏
    slide.addShape(pres.shapes.RECTANGLE, {
      x: x + 0.12, y: y + 0.12, w: 2.76, h: 0.25,
      fill: { color: s.color }, line: { color: s.color }
    });
    slide.addText(s.title, {
      x: x + 0.12, y: y + 0.12, w: 2.76, h: 0.25,
      fontSize: 9, bold: true, color: C.white, align: "center", valign: "middle",
      fontFace: "Microsoft YaHei", margin: 0
    });
    // 模拟内容行
    [0, 1, 2].forEach(li => {
      slide.addShape(pres.shapes.RECTANGLE, {
        x: x + 0.2, y: y + 0.5 + li * 0.22, w: 2.4, h: 0.16,
        fill: { color: s.color, transparency: 75 }, line: { color: "none" }
      });
    });

    // 描述
    slide.addText(s.desc, {
      x: x + 0.1, y: y + 1.3, w: 2.8, h: 0.58,
      fontSize: 9.5, color: C.gray, align: "center", fontFace: "Microsoft YaHei",
      valign: "middle", margin: 0
    });
  });
}

// ================================================================
// Slide 11 - 算法设计思路：密码安全 + 权限模型
// ================================================================
{
  const slide = pres.addSlide();
  slide.background = { color: C.lightGray };
  addSlideTitle(slide, "05  算法设计思路 — 安全机制与数据初始化", "Algorithm Design: Security & Initialization");

  // 密码加密方案
  addCard(slide, 0.3, 1.2, 4.5, 2.4);
  slide.addShape(pres.shapes.RECTANGLE, {
    x: 0.3, y: 1.2, w: 4.5, h: 0.45,
    fill: { color: C.secondary }, line: { color: C.secondary }
  });
  slide.addText("密码安全：BCrypt 加密策略", {
    x: 0.3, y: 1.2, w: 4.5, h: 0.45,
    fontSize: 12, bold: true, color: C.white, align: "center", valign: "middle",
    fontFace: "Microsoft YaHei", margin: 0
  });

  const bcryptPoints = [
    "统一使用 Spring BCryptPasswordEncoder（$2a$ 前缀）",
    "拒绝 Hutool BCrypt（$2b$ 前缀），两者哈希不兼容",
    "DataInitializer 启动自动检测 $2b$ 前缀并重置密码",
    "管理员重置密码接口：PUT /admin/college/{id}/password",
  ];
  bcryptPoints.forEach((p, i) => {
    slide.addShape(pres.shapes.OVAL, {
      x: 0.45, y: 1.78 + i * 0.42, w: 0.22, h: 0.22,
      fill: { color: C.secondary }, line: { color: C.secondary }
    });
    slide.addText(p, {
      x: 0.77, y: 1.73 + i * 0.42, w: 3.85, h: 0.38,
      fontSize: 10, color: C.dark, fontFace: "Microsoft YaHei",
      valign: "middle", margin: 0
    });
  });

  // DataInitializer 流程
  addCard(slide, 5.0, 1.2, 4.7, 2.4);
  slide.addShape(pres.shapes.RECTANGLE, {
    x: 5.0, y: 1.2, w: 4.7, h: 0.45,
    fill: { color: C.green }, line: { color: C.green }
  });
  slide.addText("DataInitializer 启动自修复", {
    x: 5.0, y: 1.2, w: 4.7, h: 0.45,
    fontSize: 12, bold: true, color: C.white, align: "center", valign: "middle",
    fontFace: "Microsoft YaHei", margin: 0
  });

  const initSteps = [
    "应用启动 → @PostConstruct 触发",
    "检测 admin 账号，不存在则创建",
    "遍历所有用户，检测 $2b$ 前缀密码",
    "发现异常密码 → 重置为 Spring BCrypt",
    "确保测试账号密码与系统一致",
  ];
  initSteps.forEach((s, i) => {
    const y = 1.72 + i * 0.42;
    slide.addShape(pres.shapes.RECTANGLE, {
      x: 5.15, y: y + 0.05, w: 0.28, h: 0.28,
      fill: { color: i < 2 ? C.green : C.orange }, line: { color: i < 2 ? C.green : C.orange }
    });
    slide.addText(String(i + 1), {
      x: 5.15, y: y + 0.05, w: 0.28, h: 0.28,
      fontSize: 9, bold: true, color: C.white, align: "center", valign: "middle",
      fontFace: "Arial", margin: 0
    });
    slide.addText(s, {
      x: 5.53, y, w: 4.0, h: 0.38,
      fontSize: 10, color: C.dark, fontFace: "Microsoft YaHei",
      valign: "middle", margin: 0
    });
  });

  // 下半：CORS + 白名单配置
  addCard(slide, 0.3, 3.75, 4.5, 1.65);
  slide.addShape(pres.shapes.RECTANGLE, {
    x: 0.3, y: 3.75, w: 4.5, h: 0.45,
    fill: { color: C.orange }, line: { color: C.orange }
  });
  slide.addText("CORS 跨域 & API 白名单配置", {
    x: 0.3, y: 3.75, w: 4.5, h: 0.45,
    fontSize: 12, bold: true, color: C.white, align: "center", valign: "middle",
    fontFace: "Microsoft YaHei", margin: 0
  });
  const corsItems = [
    "WebMvcConfigurer 全局 CORS：允许 http://localhost:5173",
    "白名单：/user/login、/user/register、/job/list、/company/list 等",
    "JwtAuthFilter 正则过滤：/company/{纯数字} 公开详情不需鉴权",
  ];
  corsItems.forEach((item, i) => {
    slide.addText("• " + item, {
      x: 0.5, y: 4.25 + i * 0.38, w: 4.15, h: 0.36,
      fontSize: 10, color: C.dark, fontFace: "Microsoft YaHei",
      valign: "middle", margin: 0
    });
  });

  // 下半右：技术难点
  addCard(slide, 5.0, 3.75, 4.7, 1.65);
  slide.addShape(pres.shapes.RECTANGLE, {
    x: 5.0, y: 3.75, w: 4.7, h: 0.45,
    fill: { color: C.primary }, line: { color: C.primary }
  });
  slide.addText("技术难点与解决方案", {
    x: 5.0, y: 3.75, w: 4.7, h: 0.45,
    fontSize: 12, bold: true, color: C.white, align: "center", valign: "middle",
    fontFace: "Microsoft YaHei", margin: 0
  });
  const techPoints = [
    ["MyBatis-Plus 不可变分页", "setRecords(new ArrayList<>()) 先置空再添加"],
    ["多格式时间解析", "正则区分 ISO8601（T）和传统格式（空格）"],
  ];
  techPoints.forEach(([prob, sol], i) => {
    const y = 4.25 + i * 0.58;
    slide.addText("❗ " + prob, {
      x: 5.15, y, w: 4.4, h: 0.28,
      fontSize: 10, bold: true, color: C.red, fontFace: "Microsoft YaHei",
      margin: 0
    });
    slide.addText("✓ " + sol, {
      x: 5.15, y: y + 0.28, w: 4.4, h: 0.28,
      fontSize: 10, color: C.green, fontFace: "Microsoft YaHei",
      margin: 0
    });
  });
}

// ================================================================
// Slide 12 - 系统测试与成果
// ================================================================
{
  const slide = pres.addSlide();
  slide.background = { color: C.lightGray };
  addSlideTitle(slide, "05  系统测试与运行成果", "Testing & Results");

  // API 测试统计
  const stats = [
    { num: "4", label: "用户角色", color: C.secondary },
    { num: "20+", label: "REST API接口", color: C.green },
    { num: "6", label: "核心功能模块", color: C.orange },
    { num: "100%", label: "主流程覆盖", color: C.primary },
  ];
  stats.forEach((s, i) => {
    const x = 0.3 + i * 2.35;
    addCard(slide, x, 1.2, 2.1, 1.5);
    slide.addShape(pres.shapes.RECTANGLE, {
      x, y: 1.2, w: 2.1, h: 0.08,
      fill: { color: s.color }, line: { color: s.color }
    });
    slide.addText(s.num, {
      x, y: 1.35, w: 2.1, h: 0.8,
      fontSize: 36, bold: true, color: s.color, align: "center", valign: "middle",
      fontFace: "Arial Black", margin: 0
    });
    slide.addText(s.label, {
      x, y: 2.1, w: 2.1, h: 0.45,
      fontSize: 13, color: C.gray, align: "center", fontFace: "Microsoft YaHei",
      valign: "middle", margin: 0
    });
  });

  // 测试结果表格
  addCard(slide, 0.3, 2.9, 9.4, 2.5);
  slide.addShape(pres.shapes.RECTANGLE, {
    x: 0.3, y: 2.9, w: 9.4, h: 0.45,
    fill: { color: C.primary }, line: { color: C.primary }
  });
  slide.addText("主要功能测试结果", {
    x: 0.3, y: 2.9, w: 9.4, h: 0.45,
    fontSize: 13, bold: true, color: C.white, align: "center", valign: "middle",
    fontFace: "Microsoft YaHei", margin: 0
  });

  const testRows = [
    ["测试项目", "预期结果", "实际结果", "状态"],
    ["学生注册/登录 (JWT)", "返回 Token，角色 student", "正常返回", "✓通过"],
    ["企业登录 (BCrypt)", "密码验证通过，返回 Token", "正常返回", "✓通过"],
    ["职位列表分页查询", "正确分页，企业可见自有职位全部状态", "正常返回", "✓通过"],
    ["投递申请 + 状态更新", "投递成功，状态流转正常", "正常返回", "✓通过"],
    ["管理员统计接口", "未登录可访问，返回统计数据", "正常返回", "✓通过"],
  ];

  const colWidths = [2.2, 2.7, 2.5, 1.8];
  testRows.forEach((row, ri) => {
    row.forEach((cell, ci) => {
      const x = 0.3 + colWidths.slice(0, ci).reduce((a, b) => a + b, 0);
      const y = 3.35 + ri * 0.38;
      if (ri === 0) {
        slide.addShape(pres.shapes.RECTANGLE, {
          x, y, w: colWidths[ci], h: 0.38,
          fill: { color: C.secondary }, line: { color: C.secondary }
        });
        slide.addText(cell, {
          x, y, w: colWidths[ci], h: 0.38,
          fontSize: 11, bold: true, color: C.white, align: "center", valign: "middle",
          fontFace: "Microsoft YaHei", margin: 0
        });
      } else {
        if (ri % 2 === 0) {
          slide.addShape(pres.shapes.RECTANGLE, {
            x, y, w: colWidths[ci], h: 0.38,
            fill: { color: C.lightGray }, line: { color: C.lightGray }
          });
        }
        slide.addText(cell, {
          x: x + 0.08, y, w: colWidths[ci] - 0.08, h: 0.38,
          fontSize: 10, color: cell.startsWith("✓") ? C.green : C.dark,
          fontFace: "Microsoft YaHei", valign: "middle", margin: 0
        });
      }
    });
  });
}

// ================================================================
// Slide 13 - 总结与展望
// ================================================================
{
  const slide = pres.addSlide();
  slide.background = { color: C.primary };

  // 装饰元素
  slide.addShape(pres.shapes.OVAL, {
    x: -1, y: -1, w: 4, h: 4,
    fill: { color: C.secondary, transparency: 85 }, line: { color: C.secondary, transparency: 80 }
  });
  slide.addShape(pres.shapes.OVAL, {
    x: 7.5, y: 2.5, w: 4, h: 4,
    fill: { color: C.accent, transparency: 90 }, line: { color: C.accent, transparency: 85 }
  });

  // 金色顶部线
  slide.addShape(pres.shapes.RECTANGLE, {
    x: 0, y: 0, w: 10, h: 0.07,
    fill: { color: C.accent }, line: { color: C.accent }
  });

  // 标题
  slide.addText("工作总结", {
    x: 1, y: 0.4, w: 8, h: 0.65,
    fontSize: 24, bold: true, color: C.white, align: "center",
    fontFace: "Microsoft YaHei"
  });
  slide.addText("Summary & Outlook", {
    x: 1, y: 1.0, w: 8, h: 0.35,
    fontSize: 13, color: "AECDE8", align: "center", fontFace: "Arial",
    charSpacing: 3
  });

  // 完成情况
  const achievements = [
    { icon: "✅", text: "完成前后端分离架构，RESTful API 设计规范" },
    { icon: "✅", text: "实现学生、企业、学院、管理员四角色完整功能" },
    { icon: "✅", text: "JWT + Spring Security 多角色鉴权体系" },
    { icon: "✅", text: "MyBatis-Plus 分页查询 + 复杂业务逻辑处理" },
    { icon: "✅", text: "DataInitializer 自修复机制 + 密码安全升级" },
  ];

  // 左侧完成
  addCard(slide, 0.5, 1.5, 4.3, 2.85, "162840");
  slide.addShape(pres.shapes.RECTANGLE, {
    x: 0.5, y: 1.5, w: 4.3, h: 0.42,
    fill: { color: C.green }, line: { color: C.green }
  });
  slide.addText("已完成工作", {
    x: 0.5, y: 1.5, w: 4.3, h: 0.42,
    fontSize: 13, bold: true, color: C.white, align: "center", valign: "middle",
    fontFace: "Microsoft YaHei", margin: 0
  });

  achievements.forEach((a, i) => {
    slide.addText(a.icon + "  " + a.text, {
      x: 0.65, y: 2.0 + i * 0.46, w: 4.0, h: 0.42,
      fontSize: 11, color: "CCE8FF", fontFace: "Microsoft YaHei",
      valign: "middle", margin: 0
    });
  });

  // 右侧展望
  const outlooks = [
    { icon: "🚀", text: "引入推荐算法（TF-IDF / 协同过滤）" },
    { icon: "🔔", text: "WebSocket 实时消息推送通知" },
    { icon: "📊", text: "更丰富的就业数据可视化分析" },
    { icon: "🔒", text: "接入 Redis 缓存与会话管理优化" },
    { icon: "📱", text: "移动端适配 / 小程序端扩展" },
  ];

  addCard(slide, 5.2, 1.5, 4.3, 2.85, "162840");
  slide.addShape(pres.shapes.RECTANGLE, {
    x: 5.2, y: 1.5, w: 4.3, h: 0.42,
    fill: { color: C.accent }, line: { color: C.accent }
  });
  slide.addText("未来展望", {
    x: 5.2, y: 1.5, w: 4.3, h: 0.42,
    fontSize: 13, bold: true, color: C.white, align: "center", valign: "middle",
    fontFace: "Microsoft YaHei", margin: 0
  });

  outlooks.forEach((o, i) => {
    slide.addText(o.icon + "  " + o.text, {
      x: 5.35, y: 2.0 + i * 0.46, w: 4.0, h: 0.42,
      fontSize: 11, color: "CCE8FF", fontFace: "Microsoft YaHei",
      valign: "middle", margin: 0
    });
  });

  // 底部致谢
  slide.addText("感谢指导教师 王敏 老师的悉心指导！", {
    x: 1, y: 4.55, w: 8, h: 0.45,
    fontSize: 14, bold: true, color: C.accent, align: "center",
    fontFace: "Microsoft YaHei"
  });
  slide.addText("感谢各位老师的聆听与指导！", {
    x: 1, y: 5.0, w: 8, h: 0.45,
    fontSize: 14, color: "AECDE8", align: "center",
    fontFace: "Microsoft YaHei"
  });
}

// ================================================================
// 输出文件
// ================================================================
const outPath = path.join("C:/Users/14684/WorkBuddy/20260316183546", "校园招聘系统答辩PPT.pptx");
pres.writeFile({ fileName: outPath }).then(() => {
  console.log("✅ PPT 已生成：" + outPath);
}).catch(err => {
  console.error("❌ 生成失败：", err);
});
