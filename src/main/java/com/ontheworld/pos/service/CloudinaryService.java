package com.ontheworld.pos.service;

import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {
    String uploadProductImage(MultipartFile file);
}
