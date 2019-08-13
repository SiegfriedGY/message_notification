package com.gengyu.msgnotification.service;

import com.gengyu.msgnotification.entity.EmailInfo;
import com.gengyu.msgnotification.timedJob.MyJob;
import com.gengyu.msgnotification.timedJob.MyJob2;
import com.gengyu.msgnotification.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author Siegfried GENG
 * @date 2019/8/13 - 11:29
 */
@Service
@Slf4j
public class ScheduledTaskService {

    /**
     * 无参的定时任务
     */
    public void scheduledTest(){

        Date date = new Date();
        date.setTime(date.getTime() + 20000);   /// 把当前时间加20秒

        try {
            // 1、创建调度器Scheduler
            SchedulerFactory schedulerFactory = new StdSchedulerFactory();
            Scheduler scheduler = schedulerFactory.getScheduler();
            // 2、创建JobDetail实例，并与PrintWordsJob类绑定(Job执行内容)
            JobDetail jobDetail = JobBuilder.newJob(MyJob.class)
                    .withIdentity("job1", "group1").build();
            // 3、构建Trigger实例,每隔1s执行一次
            Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger1", "triggerGroup1")
//                    .startNow()//立即生效
                    .startAt(new Date())
                    .endAt(date)
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                            .withIntervalInSeconds(3)//每隔1s执行一次
//                            .withIntervalInMinutes(3)
                            .repeatForever()).build();//一直执行

            //4、执行
            scheduler.scheduleJob(jobDetail, trigger);
            System.out.println("--------scheduler start ! ------------");
            scheduler.start();

            //睡眠
            TimeUnit.MINUTES.sleep(1);
            scheduler.shutdown();
            System.out.println("--------scheduler shutdown ! ------------");
        } catch (SchedulerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * 有参的定时任务，将EmailInfo传给定时任务类，定时任务类再调用发邮件的Service。
     * @param emailInfo
     */
    public void scheduledTask(EmailInfo emailInfo){

        try {

            String toList = emailInfo.getToList();
            String content = emailInfo.getContent();
            String subject = emailInfo.getTitle();
            String timeToSend = emailInfo.getTimeToSend();
            Date dateToSend = DateUtil.convertStrToDate(timeToSend);
            Integer interval = emailInfo.getInterval(); /// 间隔的秒数

            log.info("ScheduledTask方法接收到的参数为：{}, {}, {}, {}, {}, {}",
                    toList, content, subject, timeToSend, dateToSend, interval);

            // 1、创建调度器Scheduler
            SchedulerFactory schedulerFactory = new StdSchedulerFactory();
            Scheduler scheduler = schedulerFactory.getScheduler();
            // 2、创建JobDetail实例，并与PrintWordsJob类绑定(Job执行内容)

            JobDataMap jobDataMap = new JobDataMap();
            // 还是按照对象绑定比较好，有利于后期增加属性
            jobDataMap.put("emailInfo", emailInfo);

//            jobDataMap.put("content", content);
//            jobDataMap.put("subject", subject);
//            jobDataMap.put("toList", toList);

            JobDetail jobDetail = JobBuilder.newJob(MyJob2.class)
                    .setJobData(jobDataMap)     ///在此绑定数据，传给Job任务类
                    .withIdentity("job2", "group1").build();

            /// 在这里要进行一下startTime的校验，不能早于当前时间。只能大于等于当前时间。
            /// 且要加上几秒钟，作为程序运行时间缓冲。
            Date realDateToSend = DateUtil.validateDateTime(dateToSend);

            // 3、构建Trigger实例,每隔1s执行一次
            Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger2", "triggerGroup1")
//                    .startNow()//立即生效
                    .startAt(realDateToSend)
//                    .endAt(date)
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
//                            .withIntervalInSeconds(5)//每隔5s执行一次
                            .withIntervalInSeconds(interval)
//                            .withIntervalInMinutes(3)
                            .repeatForever()).build();//一直执行    ///这里要不要改？？？？

            //4、执行
            scheduler.scheduleJob(jobDetail, trigger);
            System.out.println("--------scheduler start ! ------------");
            scheduler.start();

            //睡眠
            TimeUnit.MINUTES.sleep(3);
            scheduler.shutdown();
            System.out.println("--------scheduler shutdown ! ------------");
        } catch (SchedulerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}