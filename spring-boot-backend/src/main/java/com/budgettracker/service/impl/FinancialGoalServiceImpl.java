package com.budgettracker.service.impl;

import com.budgettracker.dto.request.AddAmountToGoalRequest;
import com.budgettracker.dto.request.CreateFinancialGoalRequest;
import com.budgettracker.dto.request.UpdateFinancialGoalRequest;
import com.budgettracker.dto.response.FinancialGoalResponse;
import com.budgettracker.entity.FinancialGoal;
import com.budgettracker.entity.User;
import com.budgettracker.exception.BadRequestException;
import com.budgettracker.exception.NotFoundException;
import com.budgettracker.mapper.FinancialGoalMapper;
import com.budgettracker.repository.FinancialGoalRepository;
import com.budgettracker.repository.UserRepository;
import com.budgettracker.service.FinancialGoalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FinancialGoalServiceImpl implements FinancialGoalService {
    
    private final FinancialGoalRepository financialGoalRepository;
    private final UserRepository userRepository;
    private final FinancialGoalMapper financialGoalMapper;
    
    @Override
    public List<FinancialGoalResponse> getAll(Integer userId) {
        List<FinancialGoal> goals = financialGoalRepository.findByUserId(userId);
        return goals.stream()
                .map(financialGoalMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<FinancialGoalResponse> getActiveGoals(Integer userId) {
        LocalDate today = LocalDate.now();
        List<FinancialGoal> goals = financialGoalRepository.findActiveGoalsByUserId(
                userId, FinancialGoal.GoalStatus.ACTIVE, today);
        return goals.stream()
                .map(financialGoalMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<FinancialGoalResponse> getCompletedGoals(Integer userId) {
        List<FinancialGoal> goals = financialGoalRepository.findCompletedGoalsByUserId(userId);
        return goals.stream()
                .map(financialGoalMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public FinancialGoalResponse getById(Integer id) {
        FinancialGoal goal = financialGoalRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Financial Goal Tidak Ditemukan!"));
        return financialGoalMapper.toResponse(goal);
    }
    
    @Override
    @Transactional
    public FinancialGoalResponse create(Integer userId, CreateFinancialGoalRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Pengguna Tidak Ditemukan!"));
        
        // Validate deadline
        if (request.getDeadline().isBefore(LocalDate.now())) {
            throw new BadRequestException("Deadline tidak boleh di masa lalu");
        }
        
        // Validate goal type
        FinancialGoal.GoalType goalType;
        try {
            goalType = FinancialGoal.GoalType.valueOf(request.getType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Tipe goal tidak valid: " + request.getType() + 
                    ". Harus SAVINGS, INVESTMENT, PURCHASE, DEBT_PAYOFF, atau OTHER");
        }
        
        FinancialGoal goal = FinancialGoal.builder()
                .user(user)
                .name(request.getName())
                .description(request.getDescription())
                .targetAmount(request.getTargetAmount())
                .currentAmount(BigDecimal.ZERO)
                .deadline(request.getDeadline())
                .type(goalType)
                .status(FinancialGoal.GoalStatus.ACTIVE)
                .icon(request.getIcon() != null ? request.getIcon() : getDefaultIcon(goalType))
                .build();
        
        goal = financialGoalRepository.save(goal);
        return financialGoalMapper.toResponse(goal);
    }
    
    @Override
    @Transactional
    public FinancialGoalResponse update(Integer userId, Integer id, UpdateFinancialGoalRequest request) {
        FinancialGoal goal = financialGoalRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Financial Goal Tidak Ditemukan!"));
        
        // Check ownership
        if (!goal.getUser().getId().equals(userId)) {
            throw new BadRequestException("Anda tidak memiliki akses ke goal ini");
        }
        
        if (request.getName() != null) {
            goal.setName(request.getName());
        }
        
        if (request.getDescription() != null) {
            goal.setDescription(request.getDescription());
        }
        
        if (request.getTargetAmount() != null) {
            goal.setTargetAmount(request.getTargetAmount());
        }
        
        if (request.getDeadline() != null) {
            if (request.getDeadline().isBefore(LocalDate.now()) && 
                goal.getStatus() != FinancialGoal.GoalStatus.COMPLETED) {
                throw new BadRequestException("Deadline tidak boleh di masa lalu untuk goal yang aktif");
            }
            goal.setDeadline(request.getDeadline());
        }
        
        if (request.getType() != null) {
            try {
                FinancialGoal.GoalType goalType = FinancialGoal.GoalType.valueOf(request.getType().toUpperCase());
                goal.setType(goalType);
                // Update icon if not explicitly set
                if (request.getIcon() == null) {
                    goal.setIcon(getDefaultIcon(goalType));
                }
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Tipe goal tidak valid: " + request.getType());
            }
        }
        
        if (request.getStatus() != null) {
            try {
                FinancialGoal.GoalStatus status = FinancialGoal.GoalStatus.valueOf(request.getStatus().toUpperCase());
                goal.setStatus(status);
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Status goal tidak valid: " + request.getStatus());
            }
        }
        
        if (request.getIcon() != null) {
            goal.setIcon(request.getIcon());
        }
        
        if (request.getCurrentAmount() != null) {
            goal.setCurrentAmount(request.getCurrentAmount());
        }
        
        // Auto-complete if current amount >= target amount
        if (goal.getCurrentAmount().compareTo(goal.getTargetAmount()) >= 0 &&
            goal.getStatus() == FinancialGoal.GoalStatus.ACTIVE) {
            goal.setStatus(FinancialGoal.GoalStatus.COMPLETED);
            log.info("Goal {} auto-completed", goal.getId());
        }
        
        goal = financialGoalRepository.save(goal);
        return financialGoalMapper.toResponse(goal);
    }
    
    @Override
    @Transactional
    public void delete(Integer id) {
        FinancialGoal goal = financialGoalRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Financial Goal Tidak Ditemukan!"));
        financialGoalRepository.delete(goal);
    }
    
    @Override
    @Transactional
    public FinancialGoalResponse addAmount(Integer userId, Integer id, AddAmountToGoalRequest request) {
        FinancialGoal goal = financialGoalRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Financial Goal Tidak Ditemukan!"));
        
        // Check ownership
        if (!goal.getUser().getId().equals(userId)) {
            throw new BadRequestException("Anda tidak memiliki akses ke goal ini");
        }
        
        // Check if goal is active
        if (goal.getStatus() != FinancialGoal.GoalStatus.ACTIVE) {
            throw new BadRequestException("Hanya goal yang aktif yang bisa ditambahkan amount");
        }
        
        BigDecimal newAmount = goal.getCurrentAmount().add(request.getAmount());
        goal.setCurrentAmount(newAmount);
        
        // Auto-complete if target reached
        if (newAmount.compareTo(goal.getTargetAmount()) >= 0) {
            goal.setStatus(FinancialGoal.GoalStatus.COMPLETED);
            goal.setCurrentAmount(goal.getTargetAmount()); // Cap at target
            log.info("Goal {} completed by adding amount", goal.getId());
        }
        
        goal = financialGoalRepository.save(goal);
        return financialGoalMapper.toResponse(goal);
    }
    
    @Override
    @Transactional
    public FinancialGoalResponse completeGoal(Integer userId, Integer id) {
        FinancialGoal goal = financialGoalRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Financial Goal Tidak Ditemukan!"));
        
        // Check ownership
        if (!goal.getUser().getId().equals(userId)) {
            throw new BadRequestException("Anda tidak memiliki akses ke goal ini");
        }
        
        goal.setStatus(FinancialGoal.GoalStatus.COMPLETED);
        goal = financialGoalRepository.save(goal);
        return financialGoalMapper.toResponse(goal);
    }
    
    @Override
    @Transactional
    public void autoUpdateFromIncome(Integer userId, BigDecimal incomeAmount) {
        // Get all active goals that can be auto-updated
        LocalDate today = LocalDate.now();
        List<FinancialGoal> activeGoals = financialGoalRepository.findActiveGoalsByUserId(
                userId, FinancialGoal.GoalStatus.ACTIVE, today);
        
        if (activeGoals.isEmpty() || incomeAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        
        // Distribute income to goals (simple strategy: equal distribution)
        // In future, can be enhanced with priority or percentage allocation
        BigDecimal amountPerGoal = incomeAmount.divide(
                BigDecimal.valueOf(activeGoals.size()), 2, java.math.RoundingMode.HALF_UP);
        
        for (FinancialGoal goal : activeGoals) {
            BigDecimal newAmount = goal.getCurrentAmount().add(amountPerGoal);
            
            // Cap at target amount
            if (newAmount.compareTo(goal.getTargetAmount()) > 0) {
                newAmount = goal.getTargetAmount();
            }
            
            goal.setCurrentAmount(newAmount);
            
            // Auto-complete if target reached
            if (newAmount.compareTo(goal.getTargetAmount()) >= 0) {
                goal.setStatus(FinancialGoal.GoalStatus.COMPLETED);
                log.info("Goal {} auto-completed from income", goal.getId());
            }
        }
        
        financialGoalRepository.saveAll(activeGoals);
    }
    
    private String getDefaultIcon(FinancialGoal.GoalType type) {
        return switch (type) {
            case SAVINGS -> "💰";
            case INVESTMENT -> "📈";
            case PURCHASE -> "🛒";
            case DEBT_PAYOFF -> "💳";
            case OTHER -> "🎯";
        };
    }
}


