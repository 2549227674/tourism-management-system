package com.tourism.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tourism.common.Result;
import com.tourism.dto.CommentAuditRequest;
import com.tourism.dto.CommentDTO;
import com.tourism.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
public class AdminCommentController {

    private final CommentService commentService;

    @GetMapping
    public Result<Page<CommentDTO>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String targetType,
            @RequestParam(required = false) Long targetId,
            @RequestParam(required = false) String status) {
        return Result.success(commentService.listAdmin(page, pageSize, targetType, targetId, status));
    }

    @PutMapping("/{id}/audit")
    public Result<CommentDTO> audit(@PathVariable Long id,
                                    @Valid @RequestBody CommentAuditRequest request) {
        return Result.success("审核成功", commentService.audit(id, request));
    }
}
