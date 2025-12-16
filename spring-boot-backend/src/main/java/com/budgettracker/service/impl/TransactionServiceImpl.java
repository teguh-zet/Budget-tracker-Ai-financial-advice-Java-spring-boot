package com.budgettracker.service.impl;

import com.budgettracker.dto.request.CreateTransactionRequest;
import com.budgettracker.dto.request.UpdateTransactionRequest;
import com.budgettracker.dto.response.*;
import com.budgettracker.entity.Category;
import com.budgettracker.entity.Transaction;
import com.budgettracker.entity.User;
import com.budgettracker.exception.BadRequestException;
import com.budgettracker.exception.ForbiddenException;
import com.budgettracker.exception.NotFoundException;
import com.budgettracker.mapper.TransactionMapper;
import com.budgettracker.repository.CategoryRepository;
import com.budgettracker.repository.TransactionRepository;
import com.budgettracker.repository.UserRepository;
import com.budgettracker.service.FinancialGoalService;
import com.budgettracker.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionMapper transactionMapper;
    private final FinancialGoalService financialGoalService;
    
    @Override
    public PagedResponse<TransactionResponse> getAllByUser(
            Integer userId, Integer page, Integer limit, String search, String type) {
        
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("date").descending());
        Page<Transaction> transactionPage;
        
        // Parse type filter
        Transaction.TransactionType transactionType = null;
        if (type != null && !type.trim().isEmpty()) {
            try {
                transactionType = Transaction.TransactionType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Type harus 'income' atau 'expense'");
            }
        }
        
        // Build query based on search and type filters
        if (search != null && !search.trim().isEmpty() && transactionType != null) {
            // Both search and type filter
            transactionPage = transactionRepository.findByUserIdWithSearchAndType(userId, search, transactionType, pageable);
        } else if (search != null && !search.trim().isEmpty()) {
            // Only search filter
            transactionPage = transactionRepository.findByUserIdWithSearch(userId, search, pageable);
        } else if (transactionType != null) {
            // Only type filter
            transactionPage = transactionRepository.findByUserIdAndType(userId, transactionType, pageable);
        } else {
            // No filter
            transactionPage = transactionRepository.findByUserId(userId, pageable);
        }
        
        List<TransactionResponse> data = transactionPage.getContent().stream()
                .map(transactionMapper::toResponse)
                .collect(Collectors.toList());
        
        PagedResponse.PaginationInfo pagination = PagedResponse.PaginationInfo.builder()
                .total(transactionPage.getTotalElements())
                .page(page)
                .limit(limit)
                .totalPages((int) Math.ceil((double) transactionPage.getTotalElements() / limit))
                .build();
        
        return PagedResponse.<TransactionResponse>builder()
                .data(data)
                .pagination(pagination)
                .build();
    }
    
    @Override
    public TransactionResponse getById(Integer id) {
        Transaction transaction = transactionRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new NotFoundException("Data Transaksi tidak ditemukan!"));
        return transactionMapper.toResponse(transaction);
    }
    
    @Override
    @Transactional
    public TransactionResponse create(Integer userId, CreateTransactionRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User tidak ditemukan"));
        
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Category tidak ditemukan"));
        
        // Validasi expense tidak melebihi income bulan berjalan
        validateExpenseLimit(userId, request.getTransactionType(), request.getAmount(), null);
        
        Transaction transaction = Transaction.builder()
                .type(request.getTransactionType())
                .amount(request.getAmount())
                .date(request.getDate())
                .note(request.getNote())
                .user(user)
                .category(category)
                .build();
        
        transaction = transactionRepository.save(transaction);
        
        // Auto-update financial goals if this is an income transaction
        if (transaction.getType() == Transaction.TransactionType.INCOME) {
            try {
                BigDecimal incomeAmount = new BigDecimal(transaction.getAmount());
                financialGoalService.autoUpdateFromIncome(userId, incomeAmount);
            } catch (Exception e) {
                // Log error but don't fail transaction creation
                // Financial goal update is optional
            }
        }
        
        return transactionMapper.toResponse(transaction);
    }
    
    @Override
    @Transactional
    public TransactionResponse update(Integer userId, Integer id, UpdateTransactionRequest request) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Transaksi Tidak ditemukan"));
        
        if (!transaction.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Kamu tidak bisa Akses transaksi ini");
        }
        
        // Validasi expense tidak melebihi income bulan berjalan
        Transaction.TransactionType type = request.getTransactionType() != null 
            ? request.getTransactionType() 
            : transaction.getType();
        String amount = request.getAmount() != null ? request.getAmount() : transaction.getAmount();
        
        validateExpenseLimit(userId, type, amount, id);
        
        if (request.getType() != null) {
            transaction.setType(request.getTransactionType());
        }
        if (request.getAmount() != null) {
            transaction.setAmount(request.getAmount());
        }
        if (request.getDate() != null) {
            transaction.setDate(request.getDate());
        }
        if (request.getNote() != null) {
            transaction.setNote(request.getNote());
        }
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category tidak ditemukan"));
            transaction.setCategory(category);
        }
        
        transaction = transactionRepository.save(transaction);
        
        // Auto-update financial goals if this is an income transaction
        if (transaction.getType() == Transaction.TransactionType.INCOME) {
            try {
                BigDecimal incomeAmount = new BigDecimal(transaction.getAmount());
                financialGoalService.autoUpdateFromIncome(transaction.getUser().getId(), incomeAmount);
            } catch (Exception e) {
                // Log error but don't fail transaction update
            }
        }
        
        return transactionMapper.toResponse(transaction);
    }
    
    @Override
    @Transactional
    public void delete(Integer id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Transaksi Tidak ditemukan"));
        transactionRepository.delete(transaction);
    }
    
    @Override
    public MonthlyStatsResponse getMonthlySummary(Integer userId) {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());
        
        List<Transaction> transactions = transactionRepository.findByUserIdAndDateBetween(
                userId, startOfMonth, endOfMonth);
        
        int totalIncome = 0;
        int totalExpense = 0;
        
        for (Transaction tx : transactions) {
            int amount = Integer.parseInt(tx.getAmount());
            if (tx.getType() == Transaction.TransactionType.INCOME) {
                totalIncome += amount;
            } else {
                totalExpense += amount;
            }
        }
        
        int balance = totalIncome - totalExpense;
        int saving = (int) Math.floor(Math.max(0, totalIncome - totalExpense) * 0.3 + totalIncome * 0.05);
        
        return MonthlyStatsResponse.builder()
                .income(totalIncome)
                .expense(totalExpense)
                .balance(balance)
                .saving(saving)
                .build();
    }
    
    @Override
    public List<ChartDataResponse> getMonthlyChart(Integer userId) {
        LocalDate now = LocalDate.now();
        YearMonth yearMonth = YearMonth.from(now);
        LocalDate startOfMonth = yearMonth.atDay(1);
        LocalDate endOfMonth = yearMonth.atEndOfMonth();
        
        List<Transaction> transactions = transactionRepository.findByUserIdAndDateBetween(
                userId, startOfMonth, endOfMonth);
        
        int daysInMonth = yearMonth.lengthOfMonth();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        List<ChartDataResponse> chartData = new java.util.ArrayList<>();
        
        // Initialize all days
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = yearMonth.atDay(day);
            chartData.add(ChartDataResponse.builder()
                    .date(date.format(formatter))
                    .income(0)
                    .expense(0)
                    .build());
        }
        
        // Fill with transaction data
        for (Transaction tx : transactions) {
            int day = tx.getDate().getDayOfMonth();
            if (day > 0 && day <= daysInMonth) {
                ChartDataResponse dayData = chartData.get(day - 1);
                int amount = Integer.parseInt(tx.getAmount());
                if (tx.getType() == Transaction.TransactionType.INCOME) {
                    dayData.setIncome(dayData.getIncome() + amount);
                } else {
                    dayData.setExpense(dayData.getExpense() + amount);
                }
            }
        }
        
        return chartData;
    }
    
    @Override
    public List<TransactionResponse> getTodayTransactions(Integer userId) {
        LocalDate today = LocalDate.now();
        List<Transaction> transactions = transactionRepository.findByUserIdAndDateBetweenOrderByDateDesc(
                userId, today, today);
        return transactions.stream()
                .map(transactionMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public TodayExpenseStatsResponse getTodayExpenseStats(Integer userId) {
        LocalDate today = LocalDate.now();
        List<Transaction> transactions = transactionRepository.findByUserIdAndTypeAndDateBetween(
                userId, Transaction.TransactionType.EXPENSE, today, today);
        
        int total = transactions.stream()
                .mapToInt(tx -> Integer.parseInt(tx.getAmount()))
                .sum();
        
        return TodayExpenseStatsResponse.builder()
                .totalExpense(total)
                .count(transactions.size())
                .build();
    }
    
    private void validateExpenseLimit(Integer userId, Transaction.TransactionType type, 
                                     String amountStr, Integer excludeTransactionId) {
        if (type != Transaction.TransactionType.EXPENSE) {
            return;
        }
        
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());
        
        List<Transaction> transactions = transactionRepository.findByUserIdAndDateBetween(
                userId, startOfMonth, endOfMonth);
        
        int totalIncome = 0;
        int totalExpense = 0;
        
        for (Transaction tx : transactions) {
            if (excludeTransactionId != null && tx.getId().equals(excludeTransactionId)) {
                continue;
            }
            int amount = Integer.parseInt(tx.getAmount());
            if (tx.getType() == Transaction.TransactionType.INCOME) {
                totalIncome += amount;
            } else {
                totalExpense += amount;
            }
        }
        
        int amountToAdd = Integer.parseInt(amountStr);
        
        if (totalIncome < totalExpense + amountToAdd) {
            throw new BadRequestException("Income Bulan ini tidak mencukupi");
        }
    }
}

