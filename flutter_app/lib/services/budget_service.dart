import '../config/api_config.dart';
import '../models/budget_model.dart';
import '../services/api_service.dart';

class BudgetService {
  final ApiService _apiService = ApiService();

  Future<List<BudgetModel>> getAll() async {
    try {
      final response = await _apiService.get(ApiConfig.budgets);
      if (response.data['success'] == true) {
        final List<dynamic> data = response.data['data'] ?? [];
        return data.map((json) => BudgetModel.fromJson(json)).toList();
      }
      throw Exception(response.data['message'] ?? 'Failed to fetch budgets');
    } catch (e) {
      rethrow;
    }
  }

  Future<List<BudgetModel>> getActive() async {
    try {
      final response = await _apiService.get(ApiConfig.budgetsActive);
      if (response.data['success'] == true) {
        final List<dynamic> data = response.data['data'] ?? [];
        return data.map((json) => BudgetModel.fromJson(json)).toList();
      }
      throw Exception(response.data['message'] ?? 'Failed to fetch active budgets');
    } catch (e) {
      rethrow;
    }
  }

  Future<BudgetModel> getById(int id) async {
    try {
      final response = await _apiService.get('${ApiConfig.budgets}/$id');
      if (response.data['success'] == true) {
        return BudgetModel.fromJson(response.data['data']);
      }
      throw Exception(response.data['message'] ?? 'Failed to fetch budget');
    } catch (e) {
      rethrow;
    }
  }

  Future<BudgetModel> create(BudgetModel budget) async {
    try {
      final response = await _apiService.post(
        ApiConfig.budgets,
        data: budget.toJson(),
      );
      if (response.data['success'] == true) {
        return BudgetModel.fromJson(response.data['data']);
      }
      throw Exception(response.data['message'] ?? 'Failed to create budget');
    } catch (e) {
      rethrow;
    }
  }

  Future<BudgetModel> update(int id, BudgetModel budget) async {
    try {
      final response = await _apiService.put(
        '${ApiConfig.budgets}/$id',
        data: budget.toJson(),
      );
      if (response.data['success'] == true) {
        return BudgetModel.fromJson(response.data['data']);
      }
      throw Exception(response.data['message'] ?? 'Failed to update budget');
    } catch (e) {
      rethrow;
    }
  }

  Future<void> delete(int id) async {
    try {
      final response = await _apiService.delete('${ApiConfig.budgets}/$id');
      if (response.data['success'] != true) {
        throw Exception(response.data['message'] ?? 'Failed to delete budget');
      }
    } catch (e) {
      rethrow;
    }
  }
}

