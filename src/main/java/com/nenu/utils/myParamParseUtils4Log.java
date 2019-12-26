package com.nenu.utils;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;

public final class myParamParseUtils4Log {
    private static final int MAX_FIELD_LEN4LOG = 200;
    private static String[] types = {"java.lang.Integer", "java.lang.Double",
            "java.lang.Float", "java.lang.Long", "java.lang.Short",
            "java.lang.Byte", "java.lang.Boolean", "java.lang.Char",
            "java.lang.String", "int", "double", "long", "short", "byte",
            "boolean", "char", "float"};
    public static String getParamValue(JoinPoint joinPoint) {
        StringBuilder sb = new StringBuilder();
        //获取所有的参数
        Object[] args = joinPoint.getArgs();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        // 通过这获取到方法的所有参数名称的字符串数组
        String[] paramNames = methodSignature.getParameterNames();
        if (paramNames != null && paramNames.length > 0 && args != null && args.length > 0) {
            for (int idxArg = 0; idxArg < paramNames.length; idxArg++) {
                Object curArg = args[idxArg];
                sb.append(paramNames[idxArg]).append(":");
                if (null == curArg)
                    sb.append("null");
                else {
                    String typeName = curArg.getClass().getTypeName();
                    //1 判断是否是基础类型
                    if (Arrays.stream(types).anyMatch(curType -> curType.equalsIgnoreCase(typeName))) {
                        if (typeName.equalsIgnoreCase("java.lang.String")) {
                            sb.append(StringUtils.substring(curArg.toString(), 0, Math.min(MAX_FIELD_LEN4LOG,
                                    curArg.toString().length())));
                        } else {
                            sb.append(curArg);
                        }
                    }
                    else
                        //2 通过反射获取实体类属性
                        sb.append(getFieldsValue(curArg, false));
                }
                sb.append(", ");
            }
        }
        if (sb.length() > 2)
            sb.delete(sb.length() - 2, sb.length());
        return sb.toString();
    }
    //解析实体类，获取实体类中的属性
    public static String getFieldsValue(Object obj, Boolean includeNulls) {
        //通过反射获取所有的字段，getFileds()获取public的修饰的字段
        //getDeclaredFields获取private protected public修饰的字段
        Field[] fields = obj.getClass().getDeclaredFields();
        String typeName = obj.getClass().getTypeName();
        for (String t : types) {
            if (t.equals(typeName)) {
                return "";
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (Field f : fields) {
            //在反射时能访问私有变量
            f.setAccessible(true);
            int modifier = f.getModifiers();
            if (Modifier.isFinal(modifier) || Modifier.isStatic(modifier) || Modifier.isTransient(modifier))
                continue;
            try {
                Object curValue = f.get(obj);
                //if (Arrays.stream(types).anyMatch(curType -> curType.equalsIgnoreCase(f.getType().getName())))
                if (null == curValue) {
                    if (includeNulls) {
                        sb.append(f.getName()).append(" : null, ");
                        continue;
                    }
                    else
                        continue;
                }
                sb.append(f.getName()).append(" : ");
                String curValueStr = curValue.toString();
                if (f.getType().getName().equalsIgnoreCase("java.lang.String")) {
                    sb.append(StringUtils.substring(curValueStr, 0, Math.min(MAX_FIELD_LEN4LOG,
                            curValueStr.length())));
                } else {
                     sb.append(curValueStr);
                }
                sb.append(", ");
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if (sb.length() > 2)
            sb.delete(sb.length() - 2, sb.length());
        sb.append("}");
        return sb.toString();
    }
    
    private static HashMap<String, Class> map = new HashMap<String, Class>() {
        {
            put("java.lang.Integer", int.class);
            put("java.lang.Double", double.class);
            put("java.lang.Float", float.class);
            put("java.lang.Long", long.class);
            put("java.lang.Short", short.class);
            put("java.lang.Boolean", boolean.class);
            put("java.lang.Char", char.class);
        }
    };
    //返回方法的参数名
    private static String[] getFieldsName(JoinPoint joinPoint) throws ClassNotFoundException, NoSuchMethodException {
        String classType = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        Class<?>[] classes = new Class[args.length];
        for (int k = 0; k < args.length; k++) {
            if (!args[k].getClass().isPrimitive()) {
                //获取的是封装类型而不是基础类型
                String result = args[k].getClass().getName();
                Class s = map.get(result);
                classes[k] = s == null ? args[k].getClass() : s;
            }
        }
        ParameterNameDiscoverer pnd = new DefaultParameterNameDiscoverer();
        //获取指定的方法，第二个参数可以不传，但是为了防止有重载的现象，还是需要传入参数的类型
        Method method = Class.forName(classType).getMethod(methodName, classes);
        String[] parameterNames = pnd.getParameterNames(method);
        return parameterNames;
    }
}
