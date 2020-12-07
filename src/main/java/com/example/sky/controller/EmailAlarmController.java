package com.example.sky.controller;

import com.example.sky.dto.AlarmMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/alarm")
public class EmailAlarmController {

    private final JavaMailSender sender;

    @Value("${spring.mail.username}")
    private String from;

    @Value("${spring.mail.receivers}")
    private String receivers;

    /**
     * 接收skywalking服务的告警通知并发送至邮箱
     */
    //@PostMapping("/receive")
    @PostMapping("/email")
    public void receive(@RequestBody List<AlarmMessageDto> alarmList) {
        SimpleMailMessage message = new SimpleMailMessage();
        // 发送者邮箱
        message.setFrom(from);
        // 主题
        message.setSubject("skyWalking-业务告警");
        String content = getContent(alarmList);
        // 邮件内容
        message.setText(content);
        for (String receiver : receivers.split(",")) {
            // 接收者邮箱
            message.setTo(receiver);
            sender.send(message);
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        log.info(df.format(new Date()) + " 告警邮件已发送...");
    }

    private String getContent(List<AlarmMessageDto> alarmList) {
        StringBuilder sb = new StringBuilder();
        for (AlarmMessageDto dto : alarmList) {
            sb.append("scopeId: ").append(dto.getScopeId())
                    .append("\nscope: ").append(dto.getScope())
                    .append("\n目标 Scope 的实体名称: ").append(dto.getName())
                    .append("\nScope 实体的 ID: ").append(dto.getId0())
                    .append("\nid1: ").append(dto.getId1())
                    .append("\n告警规则名称: ").append(dto.getRuleName())
                    .append("\n告警消息内容: ").append(dto.getAlarmMessage())
                    .append("\n告警时间: ").append(dto.getStartTime())
                    .append("\n\n---------------\n\n");
        }
        return sb.toString();
    }
}