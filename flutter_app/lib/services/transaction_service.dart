import '../models/transaction_model.dart';
import '../services/api_service.dart';
import '../config/api_config.dart';

class TransactionService {
  final ApiService _apiService = ApiService();

  Future<List<TransactionModel>> getAll({
    int page = 1,
    int limit = 10,
    String? search,
    String? type,
  }) async {
    try {
      final queryParams = <String, dynamic>{
        'page': page,
        'limit': limit,
      };
      if (search != null && search.isNotEmpty) {
        queryParams['search'] = search;
      }
      if (type != null && type.isNotEmpty) {
        queryParams['type'] = type;
      }

      final response = await _apiService.get(
        ApiConfig.transactions,
        queryParameters: queryParams,
      );

      final List<dynamic> data = response.data['data'] ?? [];
      return data
          .where((json) => json != null && json is Map<String, dynamic>)
          .map((json) => TransactionModel.fromJson(json as Map<String, dynamic>))
          .toList();
    } catch (e) {
      rethrow;
    }
  }

  Future<TransactionModel> getById(int id) async {
    try {
      final response = await _apiService.get('${ApiConfig.transactionById}/$id');
      final data = response.data['data'];
      if (data == null || data is! Map<String, dynamic>) {
        throw Exception('Invalid response format from server');
      }
      return TransactionModel.fromJson(data);
    } catch (e) {
      rethrow;
    }
  }

  Future<TransactionModel> create({
    required String type,
    required int amount,
    required DateTime date,
    required int categoryId,
    String? note,
  }) async {
    try {
      // Backend mengharapkan amount sebagai String dan categoryId sebagai camelCase
      final response = await _apiService.post(
        ApiConfig.transactions,
        data: {
          'type': type,
          'amount': amount.toString(), // Backend mengharapkan String
          'date': date.toIso8601String().split('T')[0],
          'categoryId': categoryId, // camelCase sesuai DTO backend
          if (note != null && note.isNotEmpty) 'note': note,
        },
      );
      final data = response.data['data'];
      if (data == null || data is! Map<String, dynamic>) {
        throw Exception('Invalid response format from server');
      }
      return TransactionModel.fromJson(data);
    } catch (e) {
      rethrow;
    }
  }

  Future<void> update(
    int id, {
    String? type,
    int? amount,
    DateTime? date,
    int? categoryId,
    String? note,
  }) async {
    try {
      final data = <String, dynamic>{};
      if (type != null) data['type'] = type;
      if (amount != null) data['amount'] = amount.toString(); // Backend mengharapkan String
      if (date != null) data['date'] = date.toIso8601String().split('T')[0];
      if (categoryId != null) data['categoryId'] = categoryId; // camelCase sesuai DTO backend
      if (note != null) data['note'] = note;

      // Backend mengembalikan null untuk data saat update, jadi tidak perlu parse response
      await _apiService.put(
        '${ApiConfig.transactionById}/$id',
        data: data,
      );
    } catch (e) {
      rethrow;
    }
  }

  Future<void> delete(int id) async {
    try {
      await _apiService.delete('${ApiConfig.transactionById}/$id');
    } catch (e) {
      rethrow;
    }
  }

  Future<Map<String, dynamic>> getMonthlySummary() async {
    try {
      final response = await _apiService.get(ApiConfig.transactionMonthlySummary);
      final data = response.data['data'];
      if (data == null || data is! Map<String, dynamic>) {
        return {};
      }
      return data;
    } catch (e) {
      rethrow;
    }
  }

  Future<List<dynamic>> getMonthlyChart() async {
    try {
      final response = await _apiService.get(ApiConfig.transactionMonthlyChart);
      return response.data['data'] as List<dynamic>;
    } catch (e) {
      rethrow;
    }
  }

  Future<List<TransactionModel>> getTodayTransactions() async {
    try {
      final response = await _apiService.get(ApiConfig.transactionToday);
      final List<dynamic> data = response.data['data'] ?? [];
      return data
          .where((json) => json != null && json is Map<String, dynamic>)
          .map((json) => TransactionModel.fromJson(json as Map<String, dynamic>))
          .toList();
    } catch (e) {
      rethrow;
    }
  }

  Future<Map<String, dynamic>> getTodayExpenseStats() async {
    try {
      final response = await _apiService.get(ApiConfig.transactionTodayExpense);
      final data = response.data['data'];
      if (data == null || data is! Map<String, dynamic>) {
        return {};
      }
      return data;
    } catch (e) {
      rethrow;
    }
  }
}




