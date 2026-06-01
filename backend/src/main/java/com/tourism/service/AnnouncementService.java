package com.tourism.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tourism.dto.AnnouncementDTO;
import com.tourism.dto.AnnouncementRequest;

public interface AnnouncementService {

    Page<AnnouncementDTO> listPublic(int page, int pageSize, String keyword);

    AnnouncementDTO getPublicById(Long id);

    Page<AnnouncementDTO> listAdmin(int page, int pageSize, String keyword, String status);

    AnnouncementDTO getAdminById(Long id);

    AnnouncementDTO create(AnnouncementRequest request);

    AnnouncementDTO update(Long id, AnnouncementRequest request);

    AnnouncementDTO publish(Long id);

    AnnouncementDTO offline(Long id);

    void delete(Long id);
}
