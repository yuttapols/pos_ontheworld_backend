package com.ontheworld.pos.service.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.ontheworld.pos.exception.BadRequestException;
import com.ontheworld.pos.service.BarcodeService;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Map;

@Service
public class BarcodeServiceImpl implements BarcodeService {

    @Override
    public byte[] generatePng(String barcode, int width, int height) {
        try {
            BarcodeFormat format = detectFormat(barcode);
            BitMatrix bitMatrix = new MultiFormatWriter().encode(
                    barcode,
                    format,
                    width,
                    height,
                    Map.of(EncodeHintType.MARGIN, 10)
            );
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new BadRequestException("Failed to generate barcode image: " + e.getMessage());
        }
    }

    /**
     * EAN-13 → 13 digits, EAN-8 → 8 digits, everything else → CODE_128
     * CODE_128 supports alphanumeric (ITW-xxxxxxxxx, SKU codes, etc.)
     */
    private BarcodeFormat detectFormat(String barcode) {
        if (barcode.matches("\\d{13}")) return BarcodeFormat.EAN_13;
        if (barcode.matches("\\d{8}"))  return BarcodeFormat.EAN_8;
        return BarcodeFormat.CODE_128;
    }
}
