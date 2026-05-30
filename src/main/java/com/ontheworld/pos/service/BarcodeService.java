package com.ontheworld.pos.service;

public interface BarcodeService {
    /** Generate a PNG barcode image for the given barcode string. */
    byte[] generatePng(String barcode, int width, int height);
}
