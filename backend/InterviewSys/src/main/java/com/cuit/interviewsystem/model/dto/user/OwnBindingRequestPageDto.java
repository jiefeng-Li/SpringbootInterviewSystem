package com.cuit.interviewsystem.model.dto.user;


import com.cuit.interviewsystem.model.dto.PageDto;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OwnBindingRequestPageDto extends PageDto {
    @NotNull(message = "用户id不能为空")
    private Long userId;
    private Integer status;
}
