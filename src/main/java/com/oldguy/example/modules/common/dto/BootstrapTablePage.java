package com.oldguy.example.modules.common.dto;


import java.util.List;

/**
 * Bootstrap-table 分页Json
 *
 * @param <T>
 * @author huangrenhao
 * @version V1.0
 * @ClassName: BootstrapTablePage
 * @Description: TODO
 * @date 2018年4月28日 下午3:39:22
 */
public class BootstrapTablePage<T> {

    private List<T> rows;

    private Long total;

//    public BootstrapTablePage(Page<T> page) {
//        total = page.getTotal();
//        rows = page.getRecords();
//    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

}
