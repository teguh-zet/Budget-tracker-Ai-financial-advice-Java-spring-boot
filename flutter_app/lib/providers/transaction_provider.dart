import 'package:flutter/foundation.dart';
import '../models/transaction_model.dart';
import '../services/transaction_service.dart';

class TransactionProvider with ChangeNotifier {
  final TransactionService _transactionService = TransactionService();
  
  List<TransactionModel> _transactions = [];
  List<TransactionModel> _todayTransactions = [];
  bool _isLoading = false;
  String? _error;
  Map<String, dynamic>? _monthlySummary;
  Map<String, dynamic>? _todayExpenseStats;
  List<dynamic> _monthlyChart = [];

  List<TransactionModel> get transactions => _transactions;
  List<TransactionModel> get todayTransactions => _todayTransactions;
  bool get isLoading => _isLoading;
  String? get error => _error;
  Map<String, dynamic>? get monthlySummary => _monthlySummary;
  Map<String, dynamic>? get todayExpenseStats => _todayExpenseStats;
  List<dynamic> get monthlyChart => _monthlyChart;

  Future<void> fetchTransactions({int page = 1, int limit = 10, String? search}) async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      _transactions = await _transactionService.getAll(
        page: page,
        limit: limit,
        search: search,
      );
      _error = null;
    } catch (e) {
      _error = e.toString();
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  Future<void> fetchTodayTransactions() async {
    try {
      _todayTransactions = await _transactionService.getTodayTransactions();
      notifyListeners();
    } catch (e) {
      _error = e.toString();
      notifyListeners();
    }
  }

  Future<void> fetchMonthlySummary() async {
    try {
      _monthlySummary = await _transactionService.getMonthlySummary();
      notifyListeners();
    } catch (e) {
      _error = e.toString();
      notifyListeners();
    }
  }

  Future<void> fetchMonthlyChart() async {
    try {
      _monthlyChart = await _transactionService.getMonthlyChart();
      notifyListeners();
    } catch (e) {
      _error = e.toString();
      notifyListeners();
    }
  }

  Future<void> fetchTodayExpenseStats() async {
    try {
      _todayExpenseStats = await _transactionService.getTodayExpenseStats();
      notifyListeners();
    } catch (e) {
      _error = e.toString();
      notifyListeners();
    }
  }

  Future<bool> createTransaction({
    required String type,
    required int amount,
    required DateTime date,
    required int categoryId,
    String? note,
  }) async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      await _transactionService.create(
        type: type,
        amount: amount,
        date: date,
        categoryId: categoryId,
        note: note,
      );
      await fetchTransactions();
      await fetchTodayTransactions();
      _error = null;
      return true;
    } catch (e) {
      _error = e.toString();
      return false;
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  Future<bool> updateTransaction(
    int id, {
    String? type,
    int? amount,
    DateTime? date,
    int? categoryId,
    String? note,
  }) async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      await _transactionService.update(
        id,
        type: type,
        amount: amount,
        date: date,
        categoryId: categoryId,
        note: note,
      );
      await fetchTransactions();
      await fetchTodayTransactions();
      _error = null;
      return true;
    } catch (e) {
      _error = e.toString();
      return false;
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  Future<bool> deleteTransaction(int id) async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      await _transactionService.delete(id);
      await fetchTransactions();
      await fetchTodayTransactions();
      _error = null;
      return true;
    } catch (e) {
      _error = e.toString();
      return false;
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }
}




