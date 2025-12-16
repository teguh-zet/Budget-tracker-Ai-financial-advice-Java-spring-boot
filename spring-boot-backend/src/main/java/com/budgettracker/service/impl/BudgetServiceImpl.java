package com.budgettracker.service.impl;

import com.budgettracker.dto.request.CreateBudgetRequest;
import com.budgettracker.dto.request.UpdateBudgetRequest;
import com.budgettracker.dto.response.BudgetResponse;
import com.budgettracker.entity.Budget;
import com.budgettracker.entity.Category;
import com.budgettracker.entity.Transaction;
import com.budgettracker.entity.User;
import com.budgettracker.exception.BadRequestException;
import com.budgettracker.exception.NotFoundException;
import com.budgettracker.mapper.BudgetMapper;
import com.budgettracker.repository.BudgetRepository;
import com.budgettracker.repository.CategoryRepository;
import com.budgettracker.repository.TransactionRepository;
import com.budgettracker.repository.UserRepository;
import com.budgettracker.service.BudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {
    
    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final BudgetMapper budgetMapper;
    
    @Override
    public List<BudgetResponse> getAll(Integer userId) {
        List<Budget> budgets = budgetRepository.findByUserId(userId);
        return budgets.stream()
                .map(budget -> {
                    BigDecimal spentAmount = calculateSpentAmount(budget);
                    return budgetMapper.toResponse(budget, spentAmount);
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public BudgetResponse getById(Integer id) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Budget Tidak Ditemukan!"));
        BigDecimal spentAmount = calculateSpentAmount(budget);
        return budgetMapper.toResponse(budget, spentAmount);
    }
    
    @Override
    @Transactional
    public BudgetResponse create(Integer userId, CreateBudgetRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Pengguna Tidak Ditemukan!"));
        
        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Kategori Tidak Ditemukan!"));
        }
        
        // Validate period
        if (!request.getPeriod().equals("MONTHLY") && 
            !request.getPeriod().equals("WEEKLY") && 
            !request.getPeriod().equals("YEARLY")) {
            throw new BadRequestException("Period harus MONTHLY, WEEKLY, atau YEARLY");
        }
        
        // Set period end if not provided
        LocalDate periodEnd = request.getPeriodEnd();
        if (periodEnd == null) {
            periodEnd = calculatePeriodEnd(request.getPeriodStart(), request.getPeriod());
        }
        
        Budget budget = Budget.builder()
                .user(user)
                .category(category)
                .amount(request.getAmount())
                .period(request.getPeriod())
                .periodStart(request.getPeriodStart())
                .periodEnd(periodEnd)
                .description(request.getDescription())
                .isActive(true)
                .build();
        
        budget = budgetRepository.save(budget);
        BigDecimal spentAmount = calculateSpentAmount(budget);
        return budgetMapper.toResponse(budget, spentAmount);
    }
    
    @Override
    @Transactional
    public BudgetResponse update(Integer userId, Integer id, UpdateBudgetRequest request) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Budget Tidak Ditemukan!"));
        
        // Check ownership
        if (!budget.getUser().getId().equals(userId)) {
            throw new BadRequestException("Anda tidak memiliki akses ke budget ini");
        }
        
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Kategori Tidak Ditemukan!"));
            budget.setCategory(category);
        }
        
        if (request.getAmount() != null) {
            budget.setAmount(request.getAmount());
        }
        
        if (request.getPeriod() != null) {
            if (!request.getPeriod().equals("MONTHLY") && 
                !request.getPeriod().equals("WEEKLY") && 
                !request.getPeriod().equals("YEARLY")) {
                throw new BadRequestException("Period harus MONTHLY, WEEKLY, atau YEARLY");
            }
            budget.setPeriod(request.getPeriod());
        }
        
        if (request.getPeriodStart() != null) {
            budget.setPeriodStart(request.getPeriodStart());
        }
        
        if (request.getPeriodEnd() != null) {
            budget.setPeriodEnd(request.getPeriodEnd());
        } else if (request.getPeriodStart() != null && request.getPeriod() != null) {
            budget.setPeriodEnd(calculatePeriodEnd(budget.getPeriodStart(), budget.getPeriod()));
        }
        
        if (request.getDescription() != null) {
            budget.setDescription(request.getDescription());
        }
        
        if (request.getIsActive() != null) {
            budget.setIsActive(request.getIsActive());
        }
        
        budget = budgetRepository.save(budget);
        BigDecimal spentAmount = calculateSpentAmount(budget);
        return budgetMapper.toResponse(budget, spentAmount);
    }
    
    @Override
    @Transactional
    public void delete(Integer id) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Budget Tidak Ditemukan!"));
        budgetRepository.delete(budget);
    }
    
    @Override
    public List<BudgetResponse> getActiveBudgets(Integer userId) {
        LocalDate today = LocalDate.now();
        List<Budget> budgets = budgetRepository.findActiveBudgetsByUserIdAndDate(userId, today);
        return budgets.stream()
                .map(budget -> {
                    BigDecimal spentAmount = calculateSpentAmount(budget);
                    return budgetMapper.toResponse(budget, spentAmount);
                })
                .collect(Collectors.toList());
    }
    
    private BigDecimal calculateSpentAmount(Budget budget) {
        LocalDate startDate = budget.getPeriodStart();
        LocalDate endDate = budget.getPeriodEnd() != null ? budget.getPeriodEnd() : LocalDate.now();
        
        List<Transaction> transactions;
        if (budget.getCategory() != null) {
            // Calculate spent for specific category
            transactions = transactionRepository.findByUserIdAndCategoryIdAndTypeAndDateBetween(
                    budget.getUser().getId(),
                    budget.getCategory().getId(),
                    Transaction.TransactionType.EXPENSE,
                    startDate,
                    endDate
            );
        } else {
            // Calculate total expense for all categories in period
            transactions = transactionRepository.findByUserIdAndTypeAndDateBetween(
                    budget.getUser().getId(),
                    Transaction.TransactionType.EXPENSE,
                    startDate,
                    endDate
            );
        }
        
        return transactions.stream()
                .map(tx -> new BigDecimal(tx.getAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    private LocalDate calculatePeriodEnd(LocalDate startDate, String period) {
        return switch (period) {
            case "MONTHLY" -> startDate.plusMonths(1).minusDays(1);
            case "WEEKLY" -> startDate.plusWeeks(1).minusDays(1);
            case "YEARLY" -> startDate.plusYears(1).minusDays(1);
            default -> startDate.plusMonths(1).minusDays(1);
        };
    }
}


