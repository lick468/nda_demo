package com.nenu.domain;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "tbl_log")
public class TblLog implements Serializable {
    private static final long serialVersionUID = 2595062120491210373L;
    @Transient
    public static final int MAX_PARAM_LEN = 3000;
    @Transient
    public static final int MAX_ERRORMSG_LEN = 3000;
    @Transient
    public static final int MAX_FIELD_LEN4LOG = 500;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 类功能
     */
    @Column(name = "class_function_name")
    private String classFunctionName;

    /**
     * 方法功能
     */
    @Column(name = "method_function_name")
    private String methodFunctionName;

    /**
     * 操作人姓名
     */
    @Column(name = "oper_name")
    private String operName;

    /**
     * url
     */
    private String url;

    /**
     * 方法名
     */
    @Column(name = "method_name")
    private String methodName;

    /**
     * ip
     */
    private String ip;

    /**
     * 参数
     */
    private String params;

    /**
     * 类型
     */
    @Column(name = "access_model")
    private String accessModel;

    /**
     * 错误日志
     */
    @Column(name = "error_msg")
    private String errorMsg;

    /**
     * 操作时间
     */
    @Column(name = "oper_time")
    private Date operTime;

    /**
     * 设备类型
     */
    @Column(name = "deviceType")
    private String devicetype;

    /**
     * 设备操作系统
     */
    private String os;

    /**
     * 浏览器
     */
    private String browser;

    /**
     * 操作状态
     */
    private String status;

    /**
     * 耗时
     */
    @Column(name = "total_time")
    private Integer totalTime;

    /**
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取类功能
     *
     * @return class_function_name - 类功能
     */
    public String getClassFunctionName() {
        return classFunctionName;
    }

    /**
     * 设置类功能
     *
     * @param classFunctionName 类功能
     */
    public void setClassFunctionName(String classFunctionName) {
        this.classFunctionName = classFunctionName;
    }

    /**
     * 获取方法功能
     *
     * @return method_function_name - 方法功能
     */
    public String getMethodFunctionName() {
        return methodFunctionName;
    }

    /**
     * 设置方法功能
     *
     * @param methodFunctionName 方法功能
     */
    public void setMethodFunctionName(String methodFunctionName) {
        this.methodFunctionName = methodFunctionName;
    }

    /**
     * 获取操作人姓名
     *
     * @return oper_name - 操作人姓名
     */
    public String getOperName() {
        return operName;
    }

    /**
     * 设置操作人姓名
     *
     * @param operName 操作人姓名
     */
    public void setOperName(String operName) {
        this.operName = operName;
    }

    /**
     * 获取url
     *
     * @return url - url
     */
    public String getUrl() {
        return url;
    }

    /**
     * 设置url
     *
     * @param url url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 获取方法名
     *
     * @return method_name - 方法名
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * 设置方法名
     *
     * @param methodName 方法名
     */
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    /**
     * 获取ip
     *
     * @return ip - ip
     */
    public String getIp() {
        return ip;
    }

    /**
     * 设置ip
     *
     * @param ip ip
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * 获取参数
     *
     * @return params - 参数
     */
    public String getParams() {
        return params;
    }

    /**
     * 设置参数
     *
     * @param params 参数
     */
    public void setParams(String params) {
        this.params = params;
    }

    /**
     * 获取类型
     *
     * @return access_model - 类型
     */
    public String getAccessModel() {
        return accessModel;
    }

    /**
     * 设置类型
     *
     * @param accessModel 类型
     */
    public void setAccessModel(String accessModel) {
        this.accessModel = accessModel;
    }

    /**
     * 获取错误日志
     *
     * @return error_msg - 错误日志
     */
    public String getErrorMsg() {
        return errorMsg;
    }

    /**
     * 设置错误日志
     *
     * @param errorMsg 错误日志
     */
    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    /**
     * 获取操作时间
     *
     * @return oper_time - 操作时间
     */
    public Date getOperTime() {
        return operTime;
    }

    /**
     * 设置操作时间
     *
     * @param operTime 操作时间
     */
    public void setOperTime(Date operTime) {
        this.operTime = operTime;
    }

    /**
     * 获取设备类型
     *
     * @return deviceType - 设备类型
     */
    public String getDevicetype() {
        return devicetype;
    }

    /**
     * 设置设备类型
     *
     * @param devicetype 设备类型
     */
    public void setDevicetype(String devicetype) {
        this.devicetype = devicetype;
    }

    /**
     * 获取设备操作系统
     *
     * @return os - 设备操作系统
     */
    public String getOs() {
        return os;
    }

    /**
     * 设置设备操作系统
     *
     * @param os 设备操作系统
     */
    public void setOs(String os) {
        this.os = os;
    }

    /**
     * 获取浏览器
     *
     * @return browser - 浏览器
     */
    public String getBrowser() {
        return browser;
    }

    /**
     * 设置浏览器
     *
     * @param browser 浏览器
     */
    public void setBrowser(String browser) {
        this.browser = browser;
    }

    /**
     * 获取操作状态
     *
     * @return status - 操作状态
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置操作状态
     *
     * @param status 操作状态
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 获取耗时
     *
     * @return total_time - 耗时
     */
    public Integer getTotalTime() {
        return totalTime;
    }

    /**
     * 设置耗时
     *
     * @param totalTime 耗时
     */
    public void setTotalTime(Integer totalTime) {
        this.totalTime = totalTime;
    }
}