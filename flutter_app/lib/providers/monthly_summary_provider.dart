import 'package:flutter/foundation.dart';
import '../models/monthly_summary_model.dart';
import '../services/monthly_summary_service.dart';

class MonthlySummaryProvider with ChangeNotifier {
  final MonthlySummaryService _monthlySummaryService = MonthlySummaryService();
  
  List<MonthlySummaryModel> _monthlySummaries = [];
  bool _isLoading = false;
  String? _error;

  List<MonthlySummaryModel> get monthlySummaries => _monthlySummaries;
  bool get isLoading => _isLoading;
  String? get error => _error;

  Future<void> fetchMonthlySummaries() async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      _monthlySummaries = await _monthlySummaryService.getAll();
      _error = null;
    } catch (e) {
      _error = e.toString();
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  Future<bool> generateMonthlySummary() async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      await _monthlySummaryService.generate();
      await fetchMonthlySummaries();
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

  Future<bool> createMonthlySummary({
    required int month,
    required int year,
    required int totalIncome,
    required int totalExpense,
    required int balance,
  }) async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      await _monthlySummaryService.create(
        month: month,
        year: year,
        totalIncome: totalIncome,
        totalExpense: totalExpense,
        balance: balance,
      );
      await fetchMonthlySummaries();
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

  Future<bool> updateMonthlySummary(
    int id, {
    int? month,
    int? year,
    int? totalIncome,
    int? totalExpense,
    int? balance,
  }) async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      await _monthlySummaryService.update(
        id,
        month: month,
        year: year,
        totalIncome: totalIncome,
        totalExpense: totalExpense,
        balance: balance,
      );
      await fetchMonthlySummaries();
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

  Future<bool> deleteMonthlySummary(int id) async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      await _monthlySummaryService.delete(id);
      await fetchMonthlySummaries();
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




