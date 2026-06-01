package com.tourism.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tourism.dto.RouteDTO;
import com.tourism.dto.RouteRequest;

public interface RouteService {

    Page<RouteDTO> listPublic(int page, int pageSize, String keyword, String status);

    RouteDTO getPublicDetail(Long id);

    Page<RouteDTO> listAdmin(int page, int pageSize, String keyword, String status);

    RouteDTO getAdminDetail(Long id);

    RouteDTO create(RouteRequest request);

    RouteDTO update(Long id, RouteRequest request);

    void delete(Long id);

    void updateStatus(Long id, String status);
}
