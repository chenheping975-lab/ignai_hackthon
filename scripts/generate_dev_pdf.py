from reportlab.lib import colors
from reportlab.lib.enums import TA_LEFT
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import ParagraphStyle, getSampleStyleSheet
from reportlab.lib.units import mm
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
from reportlab.platypus import PageBreak, Paragraph, SimpleDocTemplate, Spacer, Table, TableStyle
from xml.sax.saxutils import escape


OUTPUT = "docs/pdf/黑客松管理平台-研发说明书-v0.3.pdf"
FONT_NAME = "CNFont"
FONT_PATH = "/System/Library/Fonts/Supplemental/Arial Unicode.ttf"

pdfmetrics.registerFont(TTFont(FONT_NAME, FONT_PATH))


def p(text, style):
    return Paragraph("<br/>".join(escape(part) for part in str(text).split("\n")), style)


def bullet(items, style):
    story = []
    for item in items:
        story.append(p("• " + item, style))
    return story


def make_table(rows, widths, styles):
    data = []
    for index, row in enumerate(rows):
        style = styles["table_header"] if index == 0 else styles["table"]
        data.append([p(cell, style) for cell in row])
    table = Table(data, colWidths=widths, repeatRows=1)
    table.setStyle(
        TableStyle(
            [
                ("FONTNAME", (0, 0), (-1, -1), FONT_NAME),
                ("GRID", (0, 0), (-1, -1), 0.25, colors.HexColor("#D8DCE6")),
                ("BACKGROUND", (0, 0), (-1, 0), colors.HexColor("#101217")),
                ("TEXTCOLOR", (0, 0), (-1, 0), colors.white),
                ("VALIGN", (0, 0), (-1, -1), "TOP"),
                ("LEFTPADDING", (0, 0), (-1, -1), 6),
                ("RIGHTPADDING", (0, 0), (-1, -1), 6),
                ("TOPPADDING", (0, 0), (-1, -1), 6),
                ("BOTTOMPADDING", (0, 0), (-1, -1), 6),
                ("ROWBACKGROUNDS", (0, 1), (-1, -1), [colors.white, colors.HexColor("#F8FAFD")]),
            ]
        )
    )
    return table


def styles():
    sample = getSampleStyleSheet()
    return {
        "cover_title": ParagraphStyle(
            "cover_title",
            parent=sample["Title"],
            fontName=FONT_NAME,
            fontSize=30,
            leading=39,
            textColor=colors.white,
            alignment=TA_LEFT,
        ),
        "cover_text": ParagraphStyle(
            "cover_text",
            parent=sample["Normal"],
            fontName=FONT_NAME,
            fontSize=13,
            leading=21,
            textColor=colors.HexColor("#F8E6D8"),
        ),
        "h1": ParagraphStyle(
            "h1",
            parent=sample["Heading1"],
            fontName=FONT_NAME,
            fontSize=17,
            leading=24,
            textColor=colors.HexColor("#101217"),
            spaceBefore=10,
            spaceAfter=8,
        ),
        "h2": ParagraphStyle(
            "h2",
            parent=sample["Heading2"],
            fontName=FONT_NAME,
            fontSize=12.5,
            leading=18,
            textColor=colors.HexColor("#FF7A18"),
            spaceBefore=8,
            spaceAfter=5,
        ),
        "body": ParagraphStyle(
            "body",
            parent=sample["BodyText"],
            fontName=FONT_NAME,
            fontSize=9.8,
            leading=15,
            textColor=colors.HexColor("#242832"),
            spaceAfter=5,
        ),
        "callout": ParagraphStyle(
            "callout",
            parent=sample["BodyText"],
            fontName=FONT_NAME,
            fontSize=10,
            leading=15,
            textColor=colors.HexColor("#101217"),
            backColor=colors.HexColor("#FFF4EA"),
            borderColor=colors.HexColor("#FFD4B3"),
            borderWidth=0.5,
            borderPadding=8,
            spaceAfter=8,
        ),
        "table": ParagraphStyle(
            "table",
            parent=sample["BodyText"],
            fontName=FONT_NAME,
            fontSize=8.2,
            leading=11.5,
            textColor=colors.HexColor("#242832"),
        ),
        "table_header": ParagraphStyle(
            "table_header",
            parent=sample["BodyText"],
            fontName=FONT_NAME,
            fontSize=8.4,
            leading=11.5,
            textColor=colors.white,
        ),
    }


def draw_cover(canvas, doc):
    width, height = A4
    canvas.saveState()
    canvas.setFillColor(colors.HexColor("#07080C"))
    canvas.rect(0, 0, width, height, fill=True, stroke=False)
    canvas.setFillColor(colors.HexColor("#FF7A18"))
    canvas.rect(0, height - 22 * mm, width, 22 * mm, fill=True, stroke=False)
    canvas.setFillColor(colors.HexColor("#101217"))
    canvas.rect(0, 0, width, 32 * mm, fill=True, stroke=False)
    canvas.restoreState()


def draw_page(canvas, doc):
    width, height = A4
    canvas.saveState()
    canvas.setFont(FONT_NAME, 8)
    canvas.setFillColor(colors.HexColor("#6B7280"))
    canvas.drawString(18 * mm, height - 14 * mm, "IGNAI Hackathon Platform · 研发说明书 V0.3")
    canvas.setFillColor(colors.HexColor("#FF7A18"))
    canvas.rect(18 * mm, height - 17 * mm, 20 * mm, 1.1, fill=True, stroke=False)
    canvas.setFillColor(colors.HexColor("#6B7280"))
    canvas.drawRightString(width - 18 * mm, 12 * mm, f"Page {doc.page}")
    canvas.restoreState()


def build():
    st = styles()
    doc = SimpleDocTemplate(
        OUTPUT,
        pagesize=A4,
        rightMargin=18 * mm,
        leftMargin=18 * mm,
        topMargin=22 * mm,
        bottomMargin=18 * mm,
        title="黑客松管理平台-研发说明书-v0.3",
        author="IGNAI / qianzhu",
    )
    usable = A4[0] - 36 * mm
    story = []

    story.append(Spacer(1, 72 * mm))
    story.append(p("IGNAI Hackathon Platform", st["cover_text"]))
    story.append(p("黑客松管理平台\n研发说明书 V0.3", st["cover_title"]))
    story.append(Spacer(1, 12 * mm))
    story.append(p("面向研发同学：官网与对标、产品边界、前端原型、Java Web 架构、数据库、API、业务判断。", st["cover_text"]))
    story.append(Spacer(1, 52 * mm))
    story.append(p("生成日期：2026-07-04", st["cover_text"]))
    story.append(PageBreak())

    story.append(p("1. 一句话结论", st["h1"]))
    story.append(p("Java Web 能完成实训版 MVP。先做单场黑客松闭环：活动配置、报名组队、AI 标签、审核邮件、项目提交、开放评分、作品展示和飞书备份。", st["callout"]))

    story.append(p("2. 官网与对标网站", st["h1"]))
    story.append(
        make_table(
            [
                ["类型", "地址", "关系"],
                ["IGNAI 官网", "https://www.ignai.cn/", "友链、品牌资产、首页视觉参考，不强行接入官网内容模块"],
                ["本地官网工程", "/Users/mac/qianzhu Vault/project/IGN AI 官网", "复用 logo、火炬、深色底、橙黄蓝色视觉"],
                ["对标网站", "https://aihacktour.com/products", "参考作品展示、曝光路径和互动结构，MVP 不引入视频"],
            ],
            [30 * mm, 58 * mm, usable - 88 * mm],
            st,
        )
    )

    story.append(p("3. MVP 范围", st["h1"]))
    story.extend(
        bullet(
            [
                "包含：活动配置、报名字段、个人/团队报名、AI 标签、审核、邮件草稿、队伍、项目提交、开放评分、投票、作品展示、飞书备份。",
                "不包含：评委独立角色、评委分配、多轮复杂评审、视频上传、视频外链、视频转码、官网 CMS、多租户 SaaS。",
                "评分改为开放评分，参赛者、现场观众或公开访客按活动规则参与。",
            ],
            st["body"],
        )
    )

    story.append(p("4. 架构和端口", st["h1"]))
    story.append(
        make_table(
            [
                ["服务", "端口 / 路径", "说明"],
                ["前端预览", "5173", "静态原型，可直接 http.server 预览"],
                ["Java Web", "8080", "Tomcat / Servlet API"],
                ["MySQL", "3306", "业务主库"],
                ["API 前缀", "/api", "公共、后台、项目、评分、飞书接口"],
                ["文件", "/uploads 或 /api/files/{id}", "MVP 本地备份，正式建议受控下载"],
            ],
            [32 * mm, 38 * mm, usable - 70 * mm],
            st,
        )
    )

    story.append(p("5. 模块拆分", st["h1"]))
    story.append(
        make_table(
            [
                ["模块", "核心责任"],
                ["活动配置", "活动、赛道、截止时间、报名和评分开关"],
                ["报名组队", "个人报名、团队报名、队员校验、重复报名判断"],
                ["AI 整理", "标签、摘要、组队建议、邮件草稿；不做最终审核"],
                ["审核通知", "状态流转、邮件预览、人工确认发送、发送记录"],
                ["项目提交", "作品信息、附件上传、本地备份、提交截止判断"],
                ["开放评分", "1-5 分、重复评分限制、评分统计"],
                ["飞书同步", "Base / Drive / 导出记录，失败可重试"],
            ],
            [36 * mm, usable - 36 * mm],
            st,
        )
    )

    story.append(PageBreak())
    story.append(p("6. 数据库设计摘要", st["h1"]))
    story.append(
        make_table(
            [
                ["表", "说明"],
                ["events / tracks / form_fields", "活动配置、赛道和动态字段"],
                ["registrations / registration_members", "报名主表和报名成员"],
                ["teams / team_members", "队伍和队伍成员"],
                ["projects / project_files", "作品和附件 metadata"],
                ["rating_rules / ratings / votes", "开放评分规则、评分和投票"],
                ["mail_templates / mail_logs", "邮件模板和发送记录"],
                ["ai_tasks / feishu_sync_logs / operation_logs", "AI 任务、飞书同步、管理员操作日志"],
            ],
            [58 * mm, usable - 58 * mm],
            st,
        )
    )

    story.append(p("7. 核心 API", st["h1"]))
    story.append(
        make_table(
            [
                ["方法", "路径", "说明"],
                ["POST", "/api/public/events/{id}/registrations", "提交报名"],
                ["PATCH", "/api/admin/registrations/{id}/review", "审核报名"],
                ["POST", "/api/admin/mail/drafts", "生成邮件草稿"],
                ["POST", "/api/projects", "保存作品"],
                ["POST", "/api/projects/{id}/files", "上传附件，不允许视频"],
                ["POST", "/api/public/projects/{id}/ratings", "开放评分"],
                ["POST", "/api/admin/feishu/sync", "创建飞书同步任务"],
            ],
            [22 * mm, 76 * mm, usable - 98 * mm],
            st,
        )
    )

    story.append(p("8. 关键业务判断", st["h1"]))
    story.extend(
        bullet(
            [
                "报名：活动开放、未截止、必填字段完整、同活动联系人不重复。",
                "团队：队长信息必填，同一成员不能加入多个已通过队伍。",
                "审核：管理员决定，AI 只提供标签和摘要；审核动作写操作日志。",
                "邮件：AI 可生成草稿，但发送前必须预览、编辑、人工确认。",
                "项目：提交前校验队伍、截止时间、标题、赛道、附件类型；MVP 无视频字段。",
                "评分：活动开启评分且未归档，同一来源不可重复评分。",
                "飞书：本地数据库是主数据源，飞书同步失败不阻断主流程。",
            ],
            st["body"],
        )
    )

    story.append(p("9. 文件夹系统", st["h1"]))
    story.extend(
        bullet(
            [
                "frontend/：静态前端原型。",
                "backend/java-web/：Servlet / Service / DAO / Model / Integration 后端骨架。",
                "database/schema.sql：MySQL 建表脚本。",
                "api/openapi.yaml：接口设计。",
                "docs/：PRD、概要设计、数据库设计、业务规则和 PDF。",
            ],
            st["body"],
        )
    )

    doc.build(story, onFirstPage=draw_cover, onLaterPages=draw_page)


if __name__ == "__main__":
    build()
