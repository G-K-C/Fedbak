package com.questioner.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * @ClassName:EmailSender
 * @Description:
 * @Author: Zhou Feng
 * @Date: 2018/6/7 1:07
 */
public class EmailSender {

    @Value("${spring.mail.username}")
    private String from;


    @Autowired
    private JavaMailSender sender;

    /*发送邮件的方法*/
    public void sendSimple(String to, String title, String content){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from); //发送者
        message.setTo(to); //接受者
        message.setSubject(title); //发送标题
        message.setText(content);  //发送内容
        sender.send(message);

    }
}
