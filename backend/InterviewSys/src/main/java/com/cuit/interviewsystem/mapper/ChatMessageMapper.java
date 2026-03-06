package com.cuit.interviewsystem.mapper;

import com.cuit.interviewsystem.model.entity.ChatMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cuit.interviewsystem.model.vo.ChatMessageVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author jiefe
* @description 针对表【t_chat_message(聊天记录表)】的数据库操作Mapper
* @createDate 2026-03-05 21:01:17
* @Entity generator.domain.ChatMessage
*/
@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {


    @Select("select * from t_chat_message where receive_id = #{reciveId} " +
            "and is_deleted = 0 and status = 0")
    List<ChatMessageVo> selectByReceiveId(Long receiveId);
}




