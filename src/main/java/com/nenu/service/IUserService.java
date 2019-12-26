package com.nenu.service;

import com.nenu.domain.TblOrgnization;
import com.nenu.domain.TblUserinfo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

public interface IUserService {
    List<TblUserinfo> retrieveOrgUsers(int curOrgId);
    List<TblUserinfo> retrieveUsers(int curOrgId);
    TblOrgnization getBelongedOrg(@NotBlank String userName);
    TblOrgnization getBelongedOrg(TblUserinfo user);
    TblUserinfo getUser(@NotBlank String userName);
}
