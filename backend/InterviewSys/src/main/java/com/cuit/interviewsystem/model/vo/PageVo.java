package com.cuit.interviewsystem.model.vo;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PageVo<T> {
    private List<T> list;
    private Long total;
    private Long pageSize;
    private Long pageNum;
    private Long pages;

    private PageVo(Page<T> page) {
        this.list = page.getRecords();
        this.total = page.getTotal();
        this.pageSize = page.getSize();
        this.pageNum = page.getCurrent();
        this.pages = page.getPages();
    }

    public static <T> PageVo<T> of(Page<T> page) {
        return new PageVo<>(page);
    }
}
