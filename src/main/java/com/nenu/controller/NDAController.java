package com.nenu.controller;

import com.nenu.aspect.lang.annotation.Log;
import com.nenu.domain.TblNdaitemtpl;
import com.nenu.domain.TblOrgnization;
import com.nenu.domain.TblUserinfo;
import com.nenu.mapper.TblNdaitemtplMapper;
import com.nenu.mapper.TblOrgnizationMapper;
import com.nenu.mapper.TblUserinfoMapper;
import com.nenu.service.IOrgService;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.Writer;
import java.util.*;

/**
 * NDA业务层
 */
@Controller
@Log(classFunctionDescribe = "NDA业务")
public class NDAController {
    @Autowired
    private TblUserinfoMapper tblUserinfoMapper;
    @Autowired
    private TblNdaitemtplMapper tblNdaitemtplMapper;
    
    @Resource(name = "orgservice")
    IOrgService orgService;

    /**
     * 跳转到创建NDA页面
     * @param map
     * @param session
     * @return
     */
    @GetMapping(value = "/createNDA")
    public String createNDA(ModelMap map, HttpSession session) {
        TblUserinfo currentUser = (TblUserinfo) session.getAttribute("currentUser");
        if (null == currentUser)
            return "login";
        String username = currentUser.getUsername();
        List<TblUserinfo> users = tblUserinfoMapper.selectAll();
        Map<String, Object[]> userMap = new HashMap<String, Object[]>();
        if (null != users && !username.isEmpty()) {
            users.removeIf(user -> username.equalsIgnoreCase(user.getUsername()));
            
            for(TblUserinfo curUser : users) {
                userMap.put(curUser.getUsername(), new Object[]{curUser.getName(),
                        curUser.getBelongedOrgId()});
            }
        }
        Example example =new Example(TblNdaitemtpl.class);
        example.createCriteria().andEqualTo("createusername", username);
        List<TblNdaitemtpl> tblNdaitemtpls = tblNdaitemtplMapper.selectByExample(example);
        map.put("NDAs",tblNdaitemtpls);
        map.put("users", userMap);
        Map<String, Object> orgMap = orgService.RetrieveOrgs4User(currentUser);
        map.putAll(orgMap);
    
        return "createNDA";
    }
}
