package com.ontheworld.pos.service.impl;

import com.ontheworld.pos.dto.inventory.StockRequest;
import com.ontheworld.pos.dto.inventory.StockResponse;
import com.ontheworld.pos.entity.Branch;
import com.ontheworld.pos.entity.Product;
import com.ontheworld.pos.entity.ProductStock;
import com.ontheworld.pos.entity.StockMovement;
import com.ontheworld.pos.exception.BadRequestException;
import com.ontheworld.pos.exception.EntityNotFoundException;
import com.ontheworld.pos.repository.BranchRepository;
import com.ontheworld.pos.repository.ProductRepository;
import com.ontheworld.pos.repository.ProductStockRepository;
import com.ontheworld.pos.repository.StockMovementRepository;
import com.ontheworld.pos.service.InventoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final ProductRepository productRepository;
    private final BranchRepository branchRepository;
    private final ProductStockRepository productStockRepository;
    private final StockMovementRepository stockMovementRepository;

    public InventoryServiceImpl(ProductRepository productRepository,
                                BranchRepository branchRepository,
                                ProductStockRepository productStockRepository,
                                StockMovementRepository stockMovementRepository) {
        this.productRepository = productRepository;
        this.branchRepository = branchRepository;
        this.productStockRepository = productStockRepository;
        this.stockMovementRepository = stockMovementRepository;
    }

    @Override
    @Transactional
    public void increaseStock(UUID branchId, StockRequest request) {
        Branch branch = requireBranch(branchId);
        Product product = requireProduct(request.getProductId());

        ProductStock stock = productStockRepository.findByProductAndBranch(product, branch)
                .orElseGet(() -> new ProductStock(product, branch, 0));

        stock.setQuantity(stock.getQuantity() + request.getQuantity());
        productStockRepository.save(stock);
        stockMovementRepository.save(new StockMovement(product, branch, request.getQuantity(), request.getReason(), null));
    }

    @Override
    @Transactional
    public void decreaseStock(UUID branchId, StockRequest request) {
        Branch branch = requireBranch(branchId);
        Product product = requireProduct(request.getProductId());

        ProductStock stock = productStockRepository.findByProductAndBranch(product, branch)
                .orElseThrow(() -> new EntityNotFoundException("Stock not found for product at this branch"));

        if (stock.getQuantity() < request.getQuantity()) {
            throw new BadRequestException(
                    "Insufficient stock: available " + stock.getQuantity() + ", requested " + request.getQuantity());
        }

        stock.setQuantity(stock.getQuantity() - request.getQuantity());
        productStockRepository.save(stock);
        stockMovementRepository.save(new StockMovement(product, branch, -request.getQuantity(), request.getReason(), null));
    }

    @Override
    public List<StockResponse> listStock(UUID branchId, UUID productId) {
        Branch branch = requireBranch(branchId);
        List<ProductStock> stocks;

        if (productId != null) {
            Product product = requireProduct(productId);
            stocks = productStockRepository.findByProductAndBranch(product, branch)
                    .map(List::of).orElse(List.of());
        } else {
            stocks = productStockRepository.findByBranch(branch);
        }

        return stocks.stream().map(this::toStockResponse).toList();
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private Branch requireBranch(UUID branchId) {
        return branchRepository.findById(branchId)
                .orElseThrow(() -> new EntityNotFoundException("Branch not found: " + branchId));
    }

    private Product requireProduct(UUID productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + productId));
    }

    private StockResponse toStockResponse(ProductStock stock) {
        StockResponse response = new StockResponse();
        response.setProductId(stock.getProduct().getId());
        response.setProductSku(stock.getProduct().getSku());
        response.setProductNameTh(stock.getProduct().getNameTh());
        response.setProductNameEn(stock.getProduct().getNameEn());
        response.setBranchId(stock.getBranch().getId());
        response.setBranchNameTh(stock.getBranch().getNameTh());
        response.setBranchNameEn(stock.getBranch().getNameEn());
        response.setQuantity(stock.getQuantity());
        return response;
    }
}
