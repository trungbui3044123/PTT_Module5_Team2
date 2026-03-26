package com.module5.team2.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class SendMailRequest {
    @NotEmpty(message = "Email không được để trống")
    private List<@Email(message = "Email không hợp lệ") String> to;

    @NotBlank(message = "Chủ đề không được để trống")
    @Size(max = 255, message = "Chủ đề tối đa 255 ký tự")
    private String subject;

    @NotBlank(message = "Nội dung không được để trống")
    @Size(max = 2000, message = "Nội dung quá dài")
    private String content;
}
