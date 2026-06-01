package com.tourism.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tourism.common.Result;
import com.tourism.dto.CommentCreateRequest;
import com.tourism.dto.CommentDTO;
import com.tourism.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public Result<CommentDTO> create(Authentication authentication,
                                     @Valid @RequestBody CommentCreateRequest request) {
        Long userId = (Long) authentication.getDetails();
        return Result.success("评论提交成功，等待审核", commentService.create(userId, request));
    }

    @GetMapping
    public Result<Page<CommentDTO>> list(@RequestParam String targetType,
                                         @RequestParam Long targetId,
                                         @RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(commentService.listPublic(targetType, targetId, page, pageSize));
    }
}
