package com.collab.resourceservice.service;

import com.collab.resourceservice.entity.Resource;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface ResourceService {
    Resource upload(MultipartFile file, String uploadedBy, String uploaderRole);
    List<Resource> getAll();
    Resource getById(Long id);
    void delete(Long id, String requesterRole);
}