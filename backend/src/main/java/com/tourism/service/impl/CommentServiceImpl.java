package com.tourism.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tourism.common.BusinessException;
import com.tourism.dto.CommentAuditRequest;
import com.tourism.dto.CommentCreateRequest;
import com.tourism.dto.CommentDTO;
import com.tourism.entity.ScenicSpot;
import com.tourism.entity.SysUser;
import com.tourism.entity.TourRoute;
import com.tourism.entity.TravelComment;
import com.tourism.mapper.ScenicSpotMapper;
import com.tourism.mapper.SysUserMapper;
import com.tourism.mapper.TourRouteMapper;
import com.tourism.mapper.TravelCommentMapper;
import com.tourism.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private static final Set<String> VALID_TARGET_TYPES = Set.of("SPOT", "ROUTE");
    private static final Set<String> VALID_QUERY_STATUSES = Set.of("PENDING", "APPROVED", "REJECTED");
    private static final Set<String> VALID_AUDIT_STATUSES = Set.of("APPROVED", "REJECTED");

    private final TravelCommentMapper commentMapper;
    private final ScenicSpotMapper spotMapper;
    private final TourRouteMapper routeMapper;
    private final SysUserMapper userMapper;

    @Override
    public CommentDTO create(Long userId, CommentCreateRequest request) {
        validateTargetType(request.getTargetType());
        validateRating(request.getRating());

        if ("SPOT".equals(request.getTargetType())) {
            ScenicSpot spot = spotMapper.selectById(request.getTargetId());
            if (spot == null || !"ON".equals(spot.getStatus())) {
                throw BusinessException.badRequest("景点不存在或已下架");
            }
        } else {
            TourRoute route = routeMapper.selectById(request.getTargetId());
            if (route == null || (!"OPEN".equals(route.getStatus()) && !"FULL".equals(route.getStatus()))) {
                throw BusinessException.badRequest("线路不存在或未开放");
            }
        }

        TravelComment comment = new TravelComment();
        comment.setUserId(userId);
        comment.setTargetType(request.getTargetType());
        comment.setTargetId(request.getTargetId());
        comment.setRating(request.getRating());
        comment.setContent(request.getContent());
        comment.setStatus("PENDING");
        commentMapper.insert(comment);

        return toDTO(comment, getUsername(userId), getTargetName(request.getTargetType(), request.getTargetId()));
    }

    @Override
    public Page<CommentDTO> listPublic(String targetType, Long targetId, int page, int pageSize) {
        validateTargetType(targetType);

        LambdaQueryWrapper<TravelComment> wrapper = new LambdaQueryWrapper<TravelComment>()
                .eq(TravelComment::getTargetType, targetType)
                .eq(TravelComment::getTargetId, targetId)
                .eq(TravelComment::getStatus, "APPROVED")
                .orderByDesc(TravelComment::getCreatedAt);

        Page<TravelComment> pageResult = commentMapper.selectPage(new Page<>(page, pageSize), wrapper);
        return convertPage(pageResult);
    }

    @Override
    public Page<CommentDTO> listAdmin(int page, int pageSize, String targetType, Long targetId, String status) {
        if (targetType != null && !targetType.isBlank()) {
            validateTargetType(targetType);
        }
        if (status != null && !status.isBlank()) {
            if (!VALID_QUERY_STATUSES.contains(status)) {
                throw BusinessException.badRequest("状态值只能是 PENDING、APPROVED 或 REJECTED");
            }
        }

        LambdaQueryWrapper<TravelComment> wrapper = new LambdaQueryWrapper<>();
        if (targetType != null && !targetType.isBlank()) {
            wrapper.eq(TravelComment::getTargetType, targetType);
        }
        if (targetId != null) {
            wrapper.eq(TravelComment::getTargetId, targetId);
        }
        if (status != null && !status.isBlank()) {
            wrapper.eq(TravelComment::getStatus, status);
        }
        wrapper.orderByDesc(TravelComment::getCreatedAt);

        Page<TravelComment> pageResult = commentMapper.selectPage(new Page<>(page, pageSize), wrapper);
        return convertPage(pageResult);
    }

    @Override
    public CommentDTO audit(Long id, CommentAuditRequest request) {
        if (!VALID_AUDIT_STATUSES.contains(request.getStatus())) {
            throw BusinessException.badRequest("审核状态只能是 APPROVED 或 REJECTED");
        }

        TravelComment comment = commentMapper.selectById(id);
        if (comment == null) {
            throw BusinessException.notFound("评论不存在");
        }
        if (!"PENDING".equals(comment.getStatus())) {
            throw BusinessException.badRequest("只能审核待审核状态的评论");
        }

        comment.setStatus(request.getStatus());
        comment.setAuditRemark(request.getAuditRemark());
        commentMapper.updateById(comment);

        return toDTO(comment, getUsername(comment.getUserId()),
                getTargetName(comment.getTargetType(), comment.getTargetId()));
    }

    private void validateTargetType(String targetType) {
        if (targetType == null || !VALID_TARGET_TYPES.contains(targetType)) {
            throw BusinessException.badRequest("评论类型只能是 SPOT 或 ROUTE");
        }
    }

    private void validateRating(Integer rating) {
        if (rating == null || rating < 1 || rating > 5) {
            throw BusinessException.badRequest("评分必须在 1 到 5 之间");
        }
    }

    private void validateAuditStatus(String status) {
        if (!VALID_AUDIT_STATUSES.contains(status)) {
            throw BusinessException.badRequest("审核状态只能是 APPROVED 或 REJECTED");
        }
    }

    private String getUsername(Long userId) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            return null;
        }
        return user.getNickname() != null ? user.getNickname() : user.getUsername();
    }

    private String getTargetName(String targetType, Long targetId) {
        if ("SPOT".equals(targetType)) {
            ScenicSpot spot = spotMapper.selectById(targetId);
            return spot != null ? spot.getName() : null;
        } else {
            TourRoute route = routeMapper.selectById(targetId);
            return route != null ? route.getName() : null;
        }
    }

    private CommentDTO toDTO(TravelComment comment, String username, String targetName) {
        CommentDTO dto = CommentDTO.fromEntity(comment);
        dto.setUsername(username);
        dto.setTargetName(targetName);
        return dto;
    }

    private Page<CommentDTO> convertPage(Page<TravelComment> page) {
        Map<Long, String> userMap = userMapper.selectList(null).stream()
                .collect(Collectors.toMap(SysUser::getId,
                        u -> u.getNickname() != null ? u.getNickname() : u.getUsername()));

        Page<CommentDTO> dtoPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        dtoPage.setRecords(page.getRecords().stream().map(comment -> {
            String targetName = getTargetName(comment.getTargetType(), comment.getTargetId());
            return toDTO(comment, userMap.get(comment.getUserId()), targetName);
        }).toList());
        return dtoPage;
    }
}
