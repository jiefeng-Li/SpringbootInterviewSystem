package com.cuit.interviewsystem.utils;


import com.cuit.interviewsystem.exception.BusinessException;
import com.cuit.interviewsystem.exception.ErrorEnum;
import com.cuit.interviewsystem.model.dto.PageDto;

@Deprecated
public class PageUtil {
    private static final long MAX_PAGE_SIZE = 1000;

    /**
     * 使用notnull注解校验数据，此方法已弃置
     */
    @Deprecated(forRemoval = true)
    public static <T extends PageDto> void validData(T dto) {
        if (dto == null) {
            throw new BusinessException(ErrorEnum.PARAMS_ERROR, "分页参数不能为空");
        }
        if (dto.getPageSize() == null || dto.getPageNum() == null) {
            throw new BusinessException(ErrorEnum.PARAMS_ERROR, "分页参数不能为空");
        }
        if (dto.getPageSize() <= 0 || dto.getPageNum() <= 0) {
            throw new BusinessException(ErrorEnum.PARAMS_ERROR, "分页参数不能小于等于0");
        }
        if (dto.getPageSize() > MAX_PAGE_SIZE) {
            throw new BusinessException(ErrorEnum.PARAMS_ERROR, "每页最多" + MAX_PAGE_SIZE + "条数据");
        }
    }
}



