package com.tangzhe.bike.entity;

import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;

@Entity
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nickname;

    @NotEmpty(message = "手机号不能为空")
    private String mobile;

    private String headImg;

    private Byte verifyFlag;

    private Byte enableFlag;

}