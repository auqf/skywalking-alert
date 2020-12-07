package com.example.sky.controller;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.example.sky.dto.AlarmMessageDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

/**
 * @author chengbb
 * @date 2019.12.26
 */
@RequestMapping("/alarm")
@RestController
@Slf4j
public class DingDingAlarmController {

    @Value("${spring.dingDing.secret}")
    private String secret;

    @Value("${spring.dingDing.webhook}")
    private String webhook;

    @Value("${spring.dingDing.keyword}")
    private String keyword;

    //@RequestMapping(value = "/pushData", method = RequestMethod.POST)
    @RequestMapping(value = "/dingDing", method = RequestMethod.POST)
    public void alarm(@RequestBody List<AlarmMessageDto> alarmMessageList) {
        log.info("alarmMessage:{}", alarmMessageList.toString());

        alarmMessageList.forEach(info -> {
            try {
                Long timestamp = System.currentTimeMillis();

                String stringToSign = timestamp + "\n" + secret;
                Mac mac = Mac.getInstance("HmacSHA256");
                mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));
                byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
                String sign = "&timestamp=" + timestamp + "&sign=" + URLEncoder.encode(new String(Base64.encodeBase64(signData)), "UTF-8");

                DingTalkClient client = new DefaultDingTalkClient(webhook + sign);
                OapiRobotSendRequest request = new OapiRobotSendRequest();
                request.setMsgtype("text");
                OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
                text.setContent(keyword + "-业务告警\n" + info.getAlarmMessage());
                request.setText(text);

                OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
                at.setAtMobiles(Arrays.asList("所有人"));
                request.setAt(at);

                OapiRobotSendResponse response = client.execute(request);
                log.info("execute:{}" + response.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}