import 'package:flutter/foundation.dart';
import '../models/budget_model.dart';
import '../services/budget_service.dart';

class BudgetProvider with ChangeNotifier {
  final BudgetService _budgetService = BudgetService();
  
  List<BudgetModel> _budgets = [];
  List<BudgetModel> _activeBudgets = [];
  bool _isLoading = false;
  String? _error;

  List<BudgetModel> get budgets => _budgets;
  List<BudgetModel> get activeBudgets => _activeBudgets;
  bool get isLoading => _isLoading;
  String? get error => _error;

  Future<void> fetchAll() async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      _budgets = await _budgetService.getAll();
      _error = null;
    } catch (e) {
      _error = e.toString();
      _budgets = [];
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
      _activeBudgets = await _budgetService.getActive();
      _error = null;
    } catch (e) {
      _error = e.toString();
      _activeBudgets = [];
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  Future<BudgetModel?> createBudget(BudgetModel budget) async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      final newBudget = await _budgetService.create(budget);
      _budgets.add(newBudget);
      _error = null;
      notifyListeners();
      return newBudget;
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      return null;
    } finally {
      _isLoading = false;
    }
  }

  Future<bool> updateBudget(int id, BudgetModel budget) async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      final updatedBudget = await _budgetService.update(id, budget);
      final index = _budgets.indexWhere((b) => b.id == id);
      if (index != -1) {
        _budgets[index] = updatedBudget;
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

  Future<bool> deleteBudget(int id) async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      await _budgetService.delete(id);
      _budgets.removeWhere((b) => b.id == id);
      _activeBudgets.removeWhere((b) => b.id == id);
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

