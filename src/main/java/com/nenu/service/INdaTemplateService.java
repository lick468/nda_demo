package com.nenu.service;

import com.nenu.domain.TblNdaitemtpl;
import com.nenu.domain.TblUserinfo;
import org.springframework.ui.ModelMap;

import java.util.List;
import java.util.Map;

public interface INdaTemplateService {
    List<TblNdaitemtpl> RetrieveUserNdaTemplates(TblUserinfo user);
    List<TblNdaitemtpl> RetrieveOrgNdaTemplates(TblUserinfo user, int orgId, int otherOrgid);
    List<TblNdaitemtpl> RetrieveUserNdaTemplates(TblUserinfo user, int orgId);
}
