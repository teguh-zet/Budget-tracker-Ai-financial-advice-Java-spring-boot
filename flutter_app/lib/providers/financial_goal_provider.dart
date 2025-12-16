import 'package:flutter/foundation.dart';
import '../models/financial_goal_model.dart';
import '../services/financial_goal_service.dart';

class FinancialGoalProvider with ChangeNotifier {
  final FinancialGoalService _goalService = FinancialGoalService();
  
  List<FinancialGoalModel> _goals = [];
  List<FinancialGoalModel> _activeGoals = [];
  List<FinancialGoalModel> _completedGoals = [];
  bool _isLoading = false;
  String? _error;

  List<FinancialGoalModel> get goals => _goals;
  List<FinancialGoalModel> get activeGoals => _activeGoals;
  List<FinancialGoalModel> get completedGoals => _completedGoals;
  bool get isLoading => _isLoading;
  String? get error => _error;

  Future<void> fetchAll() async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      _goals = await _goalService.getAll();
      _error = null;
    } catch (e) {
      _error = e.toString();
      _goals = [];
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  Future<void> fetchActive() async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      _activeGoals = await _goalService.getActive();
      _error = null;
    } catch (e) {
      _error = e.toString();
      _activeGoals = [];
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  Future<void> fetchCompleted() async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      _completedGoals = await _goalService.getCompleted();
      _error = null;
    } catch (e) {
      _error = e.toString();
      _completedGoals = [];
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  Future<FinancialGoalModel?> createGoal(FinancialGoalModel goal) async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      final newGoal = await _goalService.create(goal);
      _goals.add(newGoal);
      if (newGoal.status == 'ACTIVE') {
        _activeGoals.add(newGoal);
      }
      _error = null;
      notifyListeners();
      return newGoal;
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      return null;
    } finally {
      _isLoading = false;
    }
  }

  Future<bool> updateGoal(int id, FinancialGoalModel goal) async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      final updatedGoal = await _goalService.update(id, goal);
      final index = _goals.indexWhere((g) => g.id == id);
      if (index != -1) {
        _goals[index] = updatedGoal;
      }
      
      // Update active/completed lists
      _activeGoals.removeWhere((g) => g.id == id);
      _completedGoals.removeWhere((g) => g.id == id);
      if (updatedGoal.status == 'ACTIVE') {
        _activeGoals.add(updatedGoal);
      } else if (updatedGoal.status == 'COMPLETED') {
        _completedGoals.add(updatedGoal);
      }
      
      _error = null;
      notifyListeners();
      return true;
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      return false;
    } finally {
      _isLoading = false;
    }
  }

  Future<bool> deleteGoal(int id) async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      await _goalService.delete(id);
      _goals.removeWhere((g) => g.id == id);
      _activeGoals.removeWhere((g) => g.id == id);
      _completedGoals.removeWhere((g) => g.id == id);
      _error = null;
      notifyListeners();
      return true;
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      return false;
    } finally {
      _isLoading = false;
    }
  }

  Future<bool> addAmountToGoal(int id, double amount, {String? note}) async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      final updatedGoal = await _goalService.addAmount(id, amount, note: note);
      final index = _goals.indexWhere((g) => g.id == id);
      if (index != -1) {
        _goals[index] = updatedGoal;
      }
      
      // Update active list
      final activeIndex = _activeGoals.indexWhere((g) => g.id == id);
      if (activeIndex != -1) {
        if (updatedGoal.status == 'COMPLETED') {
          _activeGoals.removeAt(activeIndex);
          _completedGoals.add(updatedGoal);
        } else {
          _activeGoals[activeIndex] = updatedGoal;
        }
      }
      
      _error = null;
      notifyListeners();
      return true;
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      return false;
    } finally {
      _isLoading = false;
    }
  }

  Future<bool> completeGoal(int id) async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      final completedGoal = await _goalService.complete(id);
      final index = _goals.indexWhere((g) => g.id == id);
      if (index != -1) {
        _goals[index] = completedGoal;
      }
      
      _activeGoals.removeWhere((g) => g.id == id);
      _completedGoals.add(completedGoal);
      
      _error = null;
      notifyListeners();
      return true;
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      return false;
    } finally {
      _isLoading = false;
    }
  }

  void clearError() {
    _error = null;
    notifyListeners();
  }
}

