package com.tangzhe.bike.service;

import com.tangzhe.bike.common.exception.BikeException;
import com.tangzhe.bike.entity.User;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * Created by 唐哲
 * 2018-06-02 10:40
 */
public interface UserService {

    List<User> findAll() throws BikeException;

    String login(String data, String key);

    void modifyNickName(User user);

    void sendVercode(String mobile, String ip);

    String uploadHeadImg(MultipartFile file, long userId);
}
