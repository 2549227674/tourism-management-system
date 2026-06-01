package com.tourism.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tourism.common.BusinessException;
import com.tourism.dto.AnnouncementDTO;
import com.tourism.dto.AnnouncementRequest;
import com.tourism.entity.Announcement;
import com.tourism.mapper.AnnouncementMapper;
import com.tourism.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {

    private static final Set<String> VALID_STATUSES = Set.of("DRAFT", "PUBLISHED", "OFFLINE");

    private final AnnouncementMapper announcementMapper;

    @Override
    public Page<AnnouncementDTO> listPublic(int page, int pageSize, String keyword) {
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<Announcement>()
                .eq(Announcement::getStatus, "PUBLISHED");

        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(Announcement::getTitle, keyword);
        }
        wrapper.orderByDesc(Announcement::getPublishTime);

        Page<Announcement> pageResult = announcementMapper.selectPage(new Page<>(page, pageSize), wrapper);
        return convertPage(pageResult);
    }

    @Override
    public AnnouncementDTO getPublicById(Long id) {
        Announcement announcement = announcementMapper.selectById(id);
        if (announcement == null || !"PUBLISHED".equals(announcement.getStatus())) {
            throw BusinessException.notFound("公告不存在或未发布");
        }
        return AnnouncementDTO.fromEntity(announcement);
    }

    @Override
    public Page<AnnouncementDTO> listAdmin(int page, int pageSize, String keyword, String status) {
        if (status != null && !status.isBlank()) {
            if (!VALID_STATUSES.contains(status)) {
                throw BusinessException.badRequest("状态值只能是 DRAFT、PUBLISHED 或 OFFLINE");
            }
        }

        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(Announcement::getTitle, keyword);
        }
        if (status != null && !status.isBlank()) {
            wrapper.eq(Announcement::getStatus, status);
        }
        wrapper.orderByDesc(Announcement::getCreatedAt);

        Page<Announcement> pageResult = announcementMapper.selectPage(new Page<>(page, pageSize), wrapper);
        return convertPage(pageResult);
    }

    @Override
    public AnnouncementDTO getAdminById(Long id) {
        Announcement announcement = announcementMapper.selectById(id);
        if (announcement == null) {
            throw BusinessException.notFound("公告不存在");
        }
        return AnnouncementDTO.fromEntity(announcement);
    }

    @Override
    public AnnouncementDTO create(AnnouncementRequest request) {
        Announcement announcement = new Announcement();
        announcement.setTitle(request.getTitle());
        announcement.setContent(request.getContent());
        announcement.setStatus("DRAFT");
        announcementMapper.insert(announcement);
        return AnnouncementDTO.fromEntity(announcement);
    }

    @Override
    public AnnouncementDTO update(Long id, AnnouncementRequest request) {
        Announcement announcement = announcementMapper.selectById(id);
        if (announcement == null) {
            throw BusinessException.notFound("公告不存在");
        }
        announcement.setTitle(request.getTitle());
        announcement.setContent(request.getContent());
        announcementMapper.updateById(announcement);
        return AnnouncementDTO.fromEntity(announcement);
    }

    @Override
    public AnnouncementDTO publish(Long id) {
        Announcement announcement = announcementMapper.selectById(id);
        if (announcement == null) {
            throw BusinessException.notFound("公告不存在");
        }
        if (!"PUBLISHED".equals(announcement.getStatus())) {
            announcement.setStatus("PUBLISHED");
            announcement.setPublishTime(LocalDateTime.now());
            announcementMapper.updateById(announcement);
        }
        return AnnouncementDTO.fromEntity(announcement);
    }

    @Override
    public AnnouncementDTO offline(Long id) {
        Announcement announcement = announcementMapper.selectById(id);
        if (announcement == null) {
            throw BusinessException.notFound("公告不存在");
        }
        announcement.setStatus("OFFLINE");
        announcementMapper.updateById(announcement);
        return AnnouncementDTO.fromEntity(announcement);
    }

    @Override
    public void delete(Long id) {
        Announcement announcement = announcementMapper.selectById(id);
        if (announcement == null) {
            throw BusinessException.notFound("公告不存在");
        }
        announcementMapper.deleteById(id);
    }

    private Page<AnnouncementDTO> convertPage(Page<Announcement> page) {
        Page<AnnouncementDTO> dtoPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        dtoPage.setRecords(page.getRecords().stream()
                .map(AnnouncementDTO::fromEntity)
                .toList());
        return dtoPage;
    }
}
