package com.tangzhe.bike.controller;

import com.tangzhe.bike.common.constants.Constants;
import com.tangzhe.bike.common.constants.Parameters;
import com.tangzhe.bike.common.exception.BikeException;
import com.tangzhe.bike.common.resp.ApiResult;
import com.tangzhe.bike.common.vo.UserInfo;
import com.tangzhe.bike.entity.User;
import com.tangzhe.bike.entity.UserElement;
import com.tangzhe.bike.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * Created by 唐哲
 * 2018-06-02 10:39
 */
@RestController
@RequestMapping("/user")
public class UserController extends BaseController {

    @Autowired
    private Parameters parameters;

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    @ApiOperation(value = "查询所有用户")
    public ApiResult<List<User>> userList() {
        ApiResult<List<User>> result = new ApiResult<>();
//        try {
//            List<User> userList = userService.findAll();
//            result.setData(userList);
//        } catch (BikeException e) {
//            result.setCode(e.getStatusCode());
//            result.setMessage(e.getMessage());
//        } catch (Exception e) {
//            result.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
//            result.setMessage("系统内部错误");
//        }
        List<User> userList = userService.findAll();
        result.setData(userList);
        return result;
    }

    @GetMapping("/params")
    @ApiOperation(value = "配置参数查询")
    public ApiResult<List<Object>> getParams() {
        ApiResult<List<Object>> result = new ApiResult<>();
        List<Object> params = Arrays.asList(
                parameters.getRedisHost(),
                parameters.getRedisPort(),
                parameters.getRedisMaxIdle(),
                parameters.getRedisMaxTotal(),
                parameters.getRedisMaxWaitMillis());
        result.setData(params);
        return result;
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ApiResult<String> login(@RequestBody UserInfo userInfo) {
        ApiResult<String> result = new ApiResult<>();

        // 获取数据密文
        String data = userInfo.getData();
        // 获取密钥密文
        String key = userInfo.getKey();
        // 检验参数
        if (StringUtils.isBlank(data) || StringUtils.isBlank(key)) {
            throw new BikeException("参数校验失败");
        }
        // 调用业务返回token
        String token = userService.login(data, key);
        result.setCode(Constants.RESP_STATUS_OK);
        result.setMessage("登录成功");
        result.setData(token);

        return result;
    }

    /**
     * 修改用户昵称
     */
    @PutMapping("/modifyNickName")
    public ApiResult modifyNickName(@RequestBody User user) {
        ApiResult result = new ApiResult();
        
        // 获取当前用户
        // 从请求头获取用户token，利用token从redis获取userElement
        UserElement ue = super.getCurrentUser();
        user.setId(ue.getUserId());
        // 修改用户昵称
        userService.modifyNickName(user);

        return result;
    }

    /**
     * 发送短信验证码
     */
    @PostMapping("/sendVercode")
    public ApiResult sendVercode(@RequestBody User user, HttpServletRequest request) {
        ApiResult result = new ApiResult();
        // 调用业务发送手机短信验证码
        String ip = super.getIpFromRequest(request);
        userService.sendVercode(user.getMobile(), ip);
        return result;
    }

    /**
     * 修改头像
     */
    @PostMapping("/uploadHeadImg")
    public ApiResult<String> uploadHeadImg(@RequestParam(required = false)MultipartFile file) {
        ApiResult<String> result = new ApiResult<>();
        UserElement ue = super.getCurrentUser();
        String imgUrl = userService.uploadHeadImg(file, ue.getUserId());
        result.setMessage("上传成功");
        result.setData(imgUrl);
        return result;
    }

}
