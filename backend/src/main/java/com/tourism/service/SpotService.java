package com.tourism.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tourism.dto.SpotDTO;
import com.tourism.dto.SpotRequest;

public interface SpotService {

    Page<SpotDTO> listPublic(int page, int pageSize, String keyword, Long categoryId);

    SpotDTO getDetail(Long id);

    Page<SpotDTO> listAdmin(int page, int pageSize, String keyword, Long categoryId, String status);

    SpotDTO create(SpotRequest request);

    SpotDTO update(Long id, SpotRequest request);

    void delete(Long id);

    void updateStatus(Long id, String status);
}
