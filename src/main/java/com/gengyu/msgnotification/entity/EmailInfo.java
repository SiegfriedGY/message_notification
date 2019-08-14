package com.gengyu.msgnotification.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author Siegfried GENG
 * @date 2019/8/13 - 0:48
 */
@Data
@Table
@Entity
public class EmailInfo {

    @Id
    @GeneratedValue
    private Integer id;

    /// 用户为每次邮件任务所起的名字，须唯一。
    private String taskName;

    /// 收件人列表，支持多人，中间以逗号隔开。
    private String toList;

    /// 抄送
    private String ccList;

    /// 密送
    private String BccList;

    private String subject;

    private String content;

    ///发邮件的起始时间
    private String timeToSend;

    ///发邮件间隔时间（小时）
    private Integer interval;
}
