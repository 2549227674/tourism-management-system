package com.tourism.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tourism.dto.CommentAuditRequest;
import com.tourism.dto.CommentCreateRequest;
import com.tourism.dto.CommentDTO;

public interface CommentService {

    CommentDTO create(Long userId, CommentCreateRequest request);

    Page<CommentDTO> listPublic(String targetType, Long targetId, int page, int pageSize);

    Page<CommentDTO> listAdmin(int page, int pageSize, String targetType, Long targetId, String status);

    CommentDTO audit(Long id, CommentAuditRequest request);
}
