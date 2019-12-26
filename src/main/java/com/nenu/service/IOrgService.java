package com.nenu.service;

import com.nenu.domain.TblUserinfo;

import javax.validation.constraints.NotNull;
import java.util.Map;

public interface IOrgService {
    Map<String, Object> RetrieveOrgs4User (@NotNull TblUserinfo currentUser);
}
