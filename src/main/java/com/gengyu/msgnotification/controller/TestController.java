package com.gengyu.msgnotification.controller;

import com.gengyu.msgnotification.entity.EmailEntity;
import com.gengyu.msgnotification.entity.EmailInfo;
import com.gengyu.msgnotification.entity.EmailTaskInfo;
import com.gengyu.msgnotification.repository.EmailInfoRepository;
import com.gengyu.msgnotification.entity.ResponseEntity;
import com.gengyu.msgnotification.repository.EmailTaskInfoRepository;
import com.gengyu.msgnotification.service.EmailService;
import com.gengyu.msgnotification.service.ScheduledTaskService;
import com.gengyu.msgnotification.utils.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * @author Siegfried GENG
 * @date 2019/8/12 - 22:20
 */
@RestController
@RequestMapping("/email")
@Slf4j
public class TestController {

    @Autowired
    private EmailInfoRepository emailRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ScheduledTaskService scheduledTaskService;

    @Autowired
    private EmailInfoRepository emailInfoRepository;

    @Autowired
    private EmailTaskInfoRepository emailTaskInfoRepository;

    @GetMapping("/test01")
    public String test(){
        return "去你妹";
    }

/*    @GetMapping("/test02/{id}")
    public ResponseEntity<EmailInfoRepository> getEmail(@PathVariable("id")Integer id){

        log.info("传入的id：{}", id);
//        System.out.println("id="+id);
        Optional<EmailEntity> optionalEmailEntity = emailRepository.findById(id);
        EmailEntity emailEntity = optionalEmailEntity.get();
        if(emailEntity != null){
            return ResultUtil.succeeded(emailEntity);
        }
        return ResultUtil.failed();
    }*/

    @GetMapping("/test03")
    public String test03(){
        emailService.sendPlainEmail();
        log.info("已发送");
        return "已发送";
    }

    @PostMapping("/test04")
    public String test04(@RequestBody EmailInfo emailInfo){
        log.info("传入的EmailInfo为：{}", emailInfo);
        emailService.sendRichEmail(emailInfo);
        log.info("已发送富文本邮件");
        return "已发送富文本邮件";
    }

    @GetMapping("/test05")
    public String test05(){
        scheduledTaskService.scheduledTest();
        return "test05";
    }

    /**
     *  定时发邮件，指定开始时间和间隔时间，
     *  并把这一个任务单元存入数据库
     * @param emailInfo
     * @return
     */
    @PostMapping("/test06")
    public String test06(@RequestBody EmailInfo emailInfo){

        log.info("接收到的前端emailInfo参数为：", emailInfo);
        if (emailInfo != null) {
            scheduledTaskService.scheduledTask(emailInfo);
        }
        return "test06";
    }

    /**
     * 删除指定名字的任务，实际会用
     * @param taskName
     * @return
     */
    @GetMapping("/test07_1")
    public String test071(@RequestParam("taskName") String taskName){

        log.info("传入的taskName为：{}", taskName);

        EmailTaskInfo emailTaskInfo = emailTaskInfoRepository.findByTaskName(taskName);

        if (null != emailTaskInfo){
            String jobName = emailTaskInfo.getJobName();
            String jobGroupName = emailTaskInfo.getJobGroupName();
            String triggerName = emailTaskInfo.getTriggerName();
            String triggerGroupName = emailTaskInfo.getTriggerGroupName();
            log.info("4个参数为：{}，{}，{}，{}", jobName, jobGroupName, triggerName, triggerGroupName);
            log.info("现在调用删除任务");
            scheduledTaskService.removeJob(jobName, jobGroupName, triggerName, triggerGroupName);
            log.info("删除任务完毕");
        } else {
            log.info("取出的emailTaskInfo为null");
        }

        return "test071";

    }

    /**
     * 删除任务，指定四个参数，实际不会用到
     * @param jobName
     * @param jobGroupName
     * @param triggerName
     * @param triggerGroupName
     * @return
     */
    @GetMapping("/test07")
    public String test07(@RequestParam("jobName") String jobName,
                         @RequestParam("jobGroupName") String jobGroupName,
                         @RequestParam("triggerName") String triggerName,
                         @RequestParam("triggerGroupName") String triggerGroupName){

        log.info("接收到的4个参数为：{}，{}，{}，{}", jobName, jobGroupName, triggerName, triggerGroupName);
        log.info("现在调用删除任务");
        scheduledTaskService.removeJob(jobName, jobGroupName, triggerName, triggerGroupName);
        log.info("删除任务完毕");
        return "test07";
    }

    /**
     * 存入emailTaskInfo
     * @param emailTaskInfo
     * @return
     */
    @PostMapping("/test08")
    public ResponseEntity<EmailTaskInfo> test08(@RequestBody EmailTaskInfo emailTaskInfo){
        log.info("传入的emailTaskInfo为：{}", emailTaskInfo);
        EmailTaskInfo emailTaskInfo1 = emailTaskInfoRepository.save(emailTaskInfo);
        log.info("存入的emailTaskInfo1为：{}",emailTaskInfo1);
        if (null != emailTaskInfo1){
            return ResultUtil.succeeded(emailTaskInfo1);
        }else{
            return ResultUtil.failed();
        }
    }

    @PostMapping("/test09")
    public ResponseEntity<EmailInfo> test09(@RequestBody EmailInfo emailInfo){
        log.info("传入的emailTaskInfo为：{}", emailInfo);
        EmailInfo emailInfo1 = emailInfoRepository.save(emailInfo);
        log.info("存入的emailTaskInfo1为：{}",emailInfo1);
        if (null != emailInfo1){
            return ResultUtil.succeeded(emailInfo1);
        }else{
            return ResultUtil.failed();
        }
    }


}
