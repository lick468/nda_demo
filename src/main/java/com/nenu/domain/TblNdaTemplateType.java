package com.nenu.domain;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "tbl_ndatpltype")
@Data
@Builder
public class TblNdaTemplateType implements Serializable {
    private static final long serialVersionUID = -8436505294446097188L;
    /**
     * ID
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 协议类型
     */
    @Column(name = "tpltype")
    private String templateType;
}