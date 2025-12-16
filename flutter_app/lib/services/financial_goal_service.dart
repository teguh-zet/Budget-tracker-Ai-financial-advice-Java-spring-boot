import '../config/api_config.dart';
import '../models/financial_goal_model.dart';
import '../services/api_service.dart';

class FinancialGoalService {
  final ApiService _apiService = ApiService();

  Future<List<FinancialGoalModel>> getAll() async {
    try {
      final response = await _apiService.get(ApiConfig.financialGoals);
      if (response.data['success'] == true) {
        final List<dynamic> data = response.data['data'] ?? [];
        return data.map((json) => FinancialGoalModel.fromJson(json)).toList();
      }
      throw Exception(response.data['message'] ?? 'Failed to fetch financial goals');
    } catch (e) {
      rethrow;
    }
  }

  Future<List<FinancialGoalModel>> getActive() async {
    try {
      final response = await _apiService.get(ApiConfig.financialGoalsActive);
      if (response.data['success'] == true) {
        final List<dynamic> data = response.data['data'] ?? [];
        return data.map((json) => FinancialGoalModel.fromJson(json)).toList();
      }
      throw Exception(response.data['message'] ?? 'Failed to fetch active goals');
    } catch (e) {
      rethrow;
    }
  }

  Future<List<FinancialGoalModel>> getCompleted() async {
    try {
      final response = await _apiService.get(ApiConfig.financialGoalsCompleted);
      if (response.data['success'] == true) {
        final List<dynamic> data = response.data['data'] ?? [];
        return data.map((json) => FinancialGoalModel.fromJson(json)).toList();
      }
      throw Exception(response.data['message'] ?? 'Failed to fetch completed goals');
    } catch (e) {
      rethrow;
    }
  }

  Future<FinancialGoalModel> getById(int id) async {
    try {
      final response = await _apiService.get('${ApiConfig.financialGoals}/$id');
      if (response.data['success'] == true) {
        return FinancialGoalModel.fromJson(response.data['data']);
      }
      throw Exception(response.data['message'] ?? 'Failed to fetch financial goal');
    } catch (e) {
      rethrow;
    }
  }

  Future<FinancialGoalModel> create(FinancialGoalModel goal) async {
    try {
      final response = await _apiService.post(
        ApiConfig.financialGoals,
        data: goal.toJson(),
      );
      if (response.data['success'] == true) {
        return FinancialGoalModel.fromJson(response.data['data']);
      }
      throw Exception(response.data['message'] ?? 'Failed to create financial goal');
    } catch (e) {
      rethrow;
    }
  }

  Future<FinancialGoalModel> update(int id, FinancialGoalModel goal) async {
    try {
      final response = await _apiService.put(
        '${ApiConfig.financialGoals}/$id',
        data: goal.toUpdateJson(),
      );
      if (response.data['success'] == true) {
        return FinancialGoalModel.fromJson(response.data['data']);
      }
      throw Exception(response.data['message'] ?? 'Failed to update financial goal');
    } catch (e) {
      rethrow;
    }
  }

  Future<void> delete(int id) async {
    try {
      final response = await _apiService.delete('${ApiConfig.financialGoals}/$id');
      if (response.data['success'] != true) {
        throw Exception(response.data['message'] ?? 'Failed to delete financial goal');
      }
    } catch (e) {
      rethrow;
    }
  }

  Future<FinancialGoalModel> addAmount(int id, double amount, {String? note}) async {
    try {
      final response = await _apiService.post(
        '${ApiConfig.financialGoals}/$id/add-amount',
        data: {
          'amount': amount,
          if (note != null) 'note': note,
        },
      );
      if (response.data['success'] == true) {
        return FinancialGoalModel.fromJson(response.data['data']);
      }
      throw Exception(response.data['message'] ?? 'Failed to add amount to goal');
    } catch (e) {
      rethrow;
    }
  }

  Future<FinancialGoalModel> complete(int id) async {
    try {
      final response = await _apiService.post(
        '${ApiConfig.financialGoals}/$id/complete',
        data: {},
      );
      if (response.data['success'] == true) {
        return FinancialGoalModel.fromJson(response.data['data']);
      }
      throw Exception(response.data['message'] ?? 'Failed to complete goal');
    } catch (e) {
      rethrow;
    }
  }
}

