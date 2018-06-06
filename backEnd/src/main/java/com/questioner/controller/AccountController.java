package com.questioner.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Date;
import java.sql.Timestamp;

import com.questioner.entity.Account;
import com.questioner.jwt.JwtAuthenticationRequest;
import com.questioner.jwt.JwtAuthenticationResponse;
import com.questioner.jwt.JwtUser;
import com.questioner.service.abs.AccountService;
import com.questioner.service.abs.AuthService;
import com.questioner.service.abs.RecommendService;
import com.questioner.util.AvatarUtil;
import com.questioner.util.AuthCode;
import com.questioner.util.ResJsonTemplate;

import org.apache.mahout.cf.taste.common.TasteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.mail.javamail.MimeMessageHelper;
import javax.mail.internet.MimeMessage;
import javax.mail.MessagingException;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@RestController
public class AccountController {
    @Autowired
    AuthService authService;
    @Autowired
    AccountService accountService;

    AuthCode authCode;

    @Autowired
    private JavaMailSender sender;


    @Autowired
    private RecommendService recommendService;
    @Value("${deployment.url}")
    private String deploymentURL ;

    @Value("${spring.mail.username}")
    private String from;

    @Value("${default.avatarUrl}")
    private String defaultAvatarUrl;

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResJsonTemplate register(@RequestBody Account addedUser)  {
        if (accountService.register(addedUser) != null) {
            return new ResJsonTemplate<>("201", "注册成功");
        } else {
            return new ResJsonTemplate<>("400", "注册失败");
        }
    }

    @RequestMapping(value = "/validateLoginUsername/{username}", method = RequestMethod.GET)
    public ResJsonTemplate validateLoginUsername(@PathVariable("username") String loginUsername) {
        return new ResJsonTemplate<>("200", accountService.validateLoginUsername(loginUsername));
    }

    @RequestMapping(value = "/auth",method = RequestMethod.POST)
    public ResJsonTemplate auth(@RequestBody JwtAuthenticationRequest authenticationRequest)
            throws Exception
    {
        final String token = authService.login(authenticationRequest.getUsername(),
                authenticationRequest.getPassword());
        JwtAuthenticationResponse authenticationResponse = new JwtAuthenticationResponse(token);
        return new ResJsonTemplate<>("200", authenticationResponse);
    }


    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/uploadAvatar", consumes = "multipart/form-data",method = RequestMethod.POST)
    public ResJsonTemplate uploadAvatar(@RequestParam("avatar") MultipartFile avatarFile){
        if(!avatarFile.isEmpty())
        {
            String relativeUrl = AvatarUtil.saveAvatar(avatarFile);
            Long userId = ((JwtUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
            Account account = accountService.getUser(userId);
            if (account != null) {
                String oldAvatarPath = account.getAvatarURL();
                if (!oldAvatarPath.equals(defaultAvatarUrl))  // if the old avatar path isn't equal to the default path, just delete it
                    AvatarUtil.deleteAvatar(oldAvatarPath, deploymentURL);
                String imgAbsoluteUrl = deploymentURL + relativeUrl;
                account.setAvatarURL(imgAbsoluteUrl);
                accountService.save(account);
                return new ResJsonTemplate<>("200", imgAbsoluteUrl);
            }
        }
        return new ResJsonTemplate<>("404","上传头像失败");
    }
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/admin",method = RequestMethod.GET)
    public ResJsonTemplate testAdmin()
    {
        return new ResJsonTemplate<>("400","you are admin");
    }


    @RequestMapping(value = "/userOwnInfo",method = RequestMethod.GET)
    public ResJsonTemplate getOwnInfo(){
        JwtUser jwtUser =(JwtUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Account account = accountService.getUser(jwtUser.getId());
        return new ResJsonTemplate<>("200",account);
    }

    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/user/profile/saveProfile", method = RequestMethod.POST)
    public ResJsonTemplate saveProfile(@RequestBody String profile) {
        JwtUser jwtUser =(JwtUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Account account = accountService.getUser(jwtUser.getId());
        account.setProfile(profile);
        accountService.save(account);
        return new ResJsonTemplate<>("200", account);
    }


    @RequestMapping(value = "/user/{userId}", method = RequestMethod.GET)
    public ResJsonTemplate getUser(@PathVariable("userId") Long userId) {
        Account account = accountService.getUser(userId);
        if (account == null) {
            return new ResJsonTemplate<>("404", null);
        }
        else {
            return new ResJsonTemplate<>("200", account);
        }
    }

    @RequestMapping(value = "/user/followersInfo/{userId}", method = RequestMethod.GET)
    public ResJsonTemplate getFollowerInfo(@PathVariable("userId") Long userId) {
        Long followerCount = accountService.getUserFollowersCount(userId);
        Long followedCount = accountService.getUserFollowedCount(userId);
        Map<String, Long> followInfo = new HashMap<>();
        followInfo.put("followerCount", followerCount);
        followInfo.put("followedCount", followedCount);
        return new ResJsonTemplate<>("200", followInfo);
    }



    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/user/hasFollow/{followedId}", method = RequestMethod.GET)
    public ResJsonTemplate hasFollow(@PathVariable("followedId") Long followedId) {
        Long userId = ((JwtUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        return new ResJsonTemplate<>("200", accountService.hasFollowUser(userId, followedId));
    }

    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/user/follow/{followedId}", method = RequestMethod.GET)
    public ResJsonTemplate followUser(@PathVariable("followedId") Long followedId) {
        Long userId = ((JwtUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        return new ResJsonTemplate<>("200", accountService.followUser(userId, followedId));
    }

    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/user/unFollow/{followedId}", method = RequestMethod.GET)
    public ResJsonTemplate unFollowUser(@PathVariable("followedId") Long followedId) {
        Long userId = ((JwtUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        return new ResJsonTemplate<>("200", accountService.unFollowUser(userId, followedId));
    }

    @RequestMapping(value = "/user/getFollowers/{userId}", method = RequestMethod.GET)
    public ResJsonTemplate getFollowers(@PathVariable("userId") Long userId,
                                        @RequestParam(value = "currentPage", defaultValue = "0") int currentPage,
                                        @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        return new ResJsonTemplate<>("200", accountService.getUserFollowers(userId, currentPage, pageSize));
    }

    @RequestMapping(value = "/user/getFollowed/{userId}", method = RequestMethod.GET)
    public ResJsonTemplate getFollowed(@PathVariable("userId") Long userId,
                                       @RequestParam(value = "currentPage", defaultValue = "0") int currentPage,
                                       @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        return new ResJsonTemplate<>("200", accountService.getUserFollowed(userId, currentPage,pageSize));
    }

    @RequestMapping(value = "/user/getPreference/{userId}", method = RequestMethod.GET)
    public ResJsonTemplate getPreference(@PathVariable("userId") Long userId,@RequestParam(value = "preferenceSize", defaultValue = "5") int preferenceSize ){
        try {
            return new ResJsonTemplate<>("200",recommendService.getPreferences(userId,preferenceSize));
        } catch (TasteException e) {
            return new ResJsonTemplate<>("500",e.getMessage());
        }
    }
    @RequestMapping(value = "/send", method = RequestMethod.PUT)
    public ResJsonTemplate sendEmail(@RequestParam(value = "mailbox") String email){
        String Code=authCode.getAuthCode();
        String title="FedBak重置密码验证码";
        String content="您本次重置密码操作的验证码为"+Code+"。有效时间5分钟，请您尽快完成操作！";
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(from);
        message.setTo(email);
        message.setSubject(title);
        message.setText(content);
        sender.send(message);

        Account account=accountService.getUserByEmail(email);
        account.setResetCode(Code);
        Timestamp outDate = new Timestamp(System.currentTimeMillis()+(long)(5*60*1000));//5分钟后过期
        account.setResetOuttime(outDate.getTime());
        accountService.save(account);
        return new ResJsonTemplate<>("201", "发送成功");

    }
    @RequestMapping(value = "/reset", method = RequestMethod.PUT)
    public ResJsonTemplate reset(@RequestParam(value = "mailbox") String email,
                                 @RequestParam(value = "password") String password,
                                 @RequestParam(value = "verificationCode") String code){
        Account account=accountService.getUserByEmail(email);
        Timestamp outDate = new Timestamp(System.currentTimeMillis());
        Long nowTime=outDate.getTime();
        if(account.getResetOuttime()>nowTime&&code.equals(account.getResetCode())){
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            account.setPassword(encoder.encode(password));
            accountService.save(account);
            return new ResJsonTemplate<>("201", "重置成功");
        }
        else
            return new ResJsonTemplate<>("400", "重置失败");
    }
    @RequestMapping(value = "/validateMailbox", method = RequestMethod.GET)
    public ResJsonTemplate validateEmail(@RequestParam(value = "mailbox") String mailbox) {
        return new ResJsonTemplate<>("200", accountService.validateEmail(mailbox));
    }
}