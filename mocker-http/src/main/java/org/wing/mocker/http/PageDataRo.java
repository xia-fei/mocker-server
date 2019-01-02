package org.wing.mocker.http;

import java.util.List;

public class PageDataRo {
    public int pageSize=10;
    public int pageNo=1;
    public int totalSize=100;
    public List<Object> resultList;
    public static PageDataRo create(List<Object> resultList){
        PageDataRo pageDataRo=new PageDataRo();
        pageDataRo.resultList=resultList;
        return pageDataRo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getPageNo() {
        return pageNo;
    }

    public int getTotalSize() {
        return totalSize;
    }

    public List<Object> getResultList() {
        return resultList;
    }
}
