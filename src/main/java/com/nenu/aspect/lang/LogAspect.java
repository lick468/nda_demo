package com.nenu.aspect.lang;


import com.nenu.aspect.lang.annotation.Log;
import com.nenu.aspect.lang.enums.BusinessStatus;
import com.nenu.domain.*;
import com.nenu.mapper.TblLogMapper;
import com.nenu.mapper.TblUserloginMapper;
import com.nenu.utils.IpUtils;
import com.nenu.utils.ServletUtils;
import eu.bitwalker.useragentutils.UserAgent;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;

/**
 * 操作记录处理
 */
@Aspect
@Component
public class LogAspect {
    @Autowired
    private TblLogMapper logMapper;


    private static final Logger log = LoggerFactory.getLogger(LogAspect.class);

    private static Long startTime;
    private static Long endTime;


    //配置织入点
    @Pointcut("@annotation(com.nenu.aspect.lang.annotation.Log)")
    public void logPointCut() {
    }


//    /**
//     * 开始请求后执行
//     * @param joinPoint 切点
//     */
//    @Before("logPointCut()")
//    public void doBefore(JoinPoint joinPoint) throws Throwable {
//        startTime =System.currentTimeMillis();
//    }
//    /**
//     * 处理完请求后执行
//     * @param joinPoint 切点
//     */
//    @AfterReturning(pointcut = "logPointCut()")
//    public void doAfterReturning(JoinPoint joinPoint){
//        handleLog(joinPoint,null);
//    }

//    /**
//     * 拦截异常操作
//     * @param joinPoint 切点
//     * @param e 异常
//     */
//    @AfterThrowing(value = "logPointCut()",throwing = "e")
//    public void doAfterThrowing(JoinPoint joinPoint,Exception e){
//        handleLog(joinPoint,e);
//    }
    protected void handleLog(final JoinPoint joinPoint,final Exception e) {
        try{
            //获得注解
            Log controllerLog = getAnnotation(joinPoint);
            Log annotation = joinPoint.getTarget().getClass().getAnnotation(Log.class);
            if(controllerLog == null) {
                return;
            }
            UserAgent userAgent = UserAgent.parseUserAgentString(ServletUtils.getRequest().getHeader("User-Agent"));
            String hostIp = IpUtils.getHostIp();
            String os = userAgent.getOperatingSystem().getName();
            String browser = userAgent.getBrowser().getName();
            String deviceType = userAgent.getOperatingSystem().getDeviceType().getName();

            TblLog operateLog = new TblLog();

            //主机地址
            operateLog.setIp(hostIp);

          //  HttpServletRequest request = ServletUtils.getRequest();
            // 接收到请求，记录请求内容
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            String requestURI = request.getRequestURI().toString();
            //请求地址
            operateLog.setUrl(requestURI);
            //浏览器类型
            operateLog.setBrowser(browser);
            // 设备类型
            operateLog.setDevicetype(deviceType);
            //操作系统类型
            operateLog.setOs(os);

            // 请求参数
            String params = Arrays.toString(joinPoint.getArgs());
            operateLog.setParams(params);

            endTime = System.currentTimeMillis();
            Long total = endTime - startTime;
            operateLog.setTotalTime(total.intValue());

            HttpSession session = ServletUtils.getSession();
            try {
                if(requestURI.indexOf("company")>=0) {
                    TblOrgnization currentCompany = (TblOrgnization) session.getAttribute("currentCompany");
                    //操作人
                    operateLog.setOperName(currentCompany.getOrgleader());
                }else {
                    TblUserinfo currentUser = (TblUserinfo) session.getAttribute("currentUser");
                    //操作人
                    operateLog.setOperName(currentUser.getUsername());
                }

            }
            catch(Exception exp) {
                exp.printStackTrace();
            }

            // 操作状态
            operateLog.setStatus(BusinessStatus.SUCCESS.name());
            if (e != null)
            {
                // 操作状态
                operateLog.setStatus(BusinessStatus.FAIL.name());
                // 错误消息
                operateLog.setErrorMsg(StringUtils.substring(e.getMessage(), 0, 2000));
            }
           //设置方法名称
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            //操作的方法
            operateLog.setMethodName(className + "." + methodName + "()");

            //获取方法级别的注解，信息，参数
            getControllerMethodDescription(controllerLog,operateLog);

            //获取类级别的注解，注解，参数
            getControllerClassDescription(annotation,operateLog);

            operateLog.setOperTime(new Date());
            //保存数据库
            logMapper.insert(operateLog);

        }catch (Exception exp){
            //记录本地异常日志
            log.error("==前置通知异常==");
            log.error("异常消息{}",exp.getMessage());
            exp.printStackTrace();
        }
    }
    public void getControllerMethodDescription(Log log, TblLog operateLog) throws Exception {
        //设置action 动作
        //业务类型
        operateLog.setAccessModel(log.businessType().name());
        //设置标题
        //模块标题
        operateLog.setMethodFunctionName(log.methodFunctionDescribe());

    }

    public void getControllerClassDescription(Log log, TblLog operateLog) throws Exception {
        // 设置类级别注解
        try {
            operateLog.setClassFunctionName(log.classFunctionDescribe());
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Log getAnnotation(JoinPoint joinPoint) throws Exception {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        if(method != null) {
            return method.getAnnotation(Log.class);
        }
        return null;
    }




    /**
     * 配置环绕通知,使用在方法logPointcut()上注册的切入点
     *
     * @param joinPoint join point for advice
     */
    @Around("logPointCut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = null;
        startTime = System.currentTimeMillis();
        UserAgent userAgent = UserAgent.parseUserAgentString(ServletUtils.getRequest().getHeader("User-Agent"));
        String hostIp = IpUtils.getHostIp();
        String os = userAgent.getOperatingSystem().getName();
        String browser = userAgent.getBrowser().getName();
        String deviceType = userAgent.getOperatingSystem().getDeviceType().getName();

        TblLog operateLog = new TblLog();
        //主机地址
        operateLog.setIp(hostIp);

        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String requestURI = request.getRequestURI();
        //请求地址
        operateLog.setUrl(requestURI);
        //浏览器类型
        operateLog.setBrowser(browser);
        // 设备类型
        operateLog.setDevicetype(deviceType);
        //操作系统类型
        operateLog.setOs(os);

        String params = Arrays.toString(joinPoint.getArgs());


        // 请求参数
        operateLog.setParams(params);

        //设置方法名称
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        //操作的方法
        operateLog.setMethodName(className + "." + methodName + "()");


        Log controllerLog = getAnnotation(joinPoint);
        Log annotation = joinPoint.getTarget().getClass().getAnnotation(Log.class);

        //获取方法级别的注解，信息，参数
        getControllerMethodDescription(controllerLog,operateLog);

        //获取类级别的注解，注解，参数
        getControllerClassDescription(annotation,operateLog);

        operateLog.setOperTime(new Date());
        HttpSession session = ServletUtils.getSession();
        try {
            if(requestURI.indexOf("login")>=0 && requestURI.indexOf("company")>=0) { // 公司管理员登录
                Object[] args = joinPoint.getArgs();
                TblOrgnization arg = (TblOrgnization) args[0];
                operateLog.setOperName(arg.getOrgleader());
            }else if(requestURI.indexOf("login")>=0 && requestURI.indexOf("admin")>=0) { // 管理员登录
                Object[] args = joinPoint.getArgs();
                TblAdmin arg = (TblAdmin) args[0];
               operateLog.setOperName(arg.getUsername());
            }else if(requestURI.indexOf("login")>=0 ) { // 用户登录
                Object[] args = joinPoint.getArgs();
                TblUserinfo arg = (TblUserinfo) args[0];
               operateLog.setOperName(arg.getUsername());
            }else if(requestURI.indexOf("company")>=0) {
                TblOrgnization currentCompany = (TblOrgnization) session.getAttribute("currentCompany");
                //操作人
                operateLog.setOperName(currentCompany.getOrgleader());
            }else if(requestURI.indexOf("admin")>=0) {
                TblAdmin admin = (TblAdmin) session.getAttribute("currentAdmin");
                //操作人
                operateLog.setOperName(admin.getUsername());
            }else {
                TblUserinfo currentUser = (TblUserinfo) session.getAttribute("currentUser");
                //操作人
                operateLog.setOperName(currentUser.getUsername());
            }

        }
        catch(Exception exp) {
            exp.printStackTrace();
        }

        // 操作状态
        operateLog.setStatus(BusinessStatus.SUCCESS.name());
        try {
            result = joinPoint.proceed();
            endTime = System.currentTimeMillis();
            Long total = endTime - startTime;
            operateLog.setTotalTime(total.intValue());
            //保存数据库
            logMapper.insert(operateLog);
        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            // 操作状态
            operateLog.setStatus(BusinessStatus.FAIL.name());

            operateLog.setErrorMsg(baos.toString().substring(0,3000));
            endTime = System.currentTimeMillis();
            Long total = endTime - startTime;
            operateLog.setTotalTime(total.intValue());
            //保存数据库
            logMapper.insert(operateLog);
            e.printStackTrace();
        }

        return result;
    }

}
