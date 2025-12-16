import '../models/monthly_summary_model.dart';
import '../services/api_service.dart';
import '../config/api_config.dart';

class MonthlySummaryService {
  final ApiService _apiService = ApiService();

  Future<List<MonthlySummaryModel>> getAll() async {
    try {
      final response = await _apiService.get(ApiConfig.monthlySummaries);
      final List<dynamic> data = response.data['data'] ?? [];
      return data
          .where((json) => json != null && json is Map<String, dynamic>)
          .map((json) => MonthlySummaryModel.fromJson(json as Map<String, dynamic>))
          .toList();
    } catch (e) {
      rethrow;
    }
  }

  Future<MonthlySummaryModel> getById(int id) async {
    try {
      final response = await _apiService.get('${ApiConfig.monthlySummaryById}/$id');
      final data = response.data['data'];
      if (data == null || data is! Map<String, dynamic>) {
        throw Exception('Invalid response format from server');
      }
      return MonthlySummaryModel.fromJson(data);
    } catch (e) {
      rethrow;
    }
  }

  Future<MonthlySummaryModel> create({
    required int month,
    required int year,
    required int totalIncome,
    required int totalExpense,
    required int balance,
  }) async {
    try {
      final response = await _apiService.post(
        ApiConfig.monthlySummaries,
        data: {
          'month': month,
          'year': year,
          'total_income': totalIncome,
          'total_expense': totalExpense,
          'balance': balance,
        },
      );
      final data = response.data['data'];
      if (data == null || data is! Map<String, dynamic>) {
        throw Exception('Invalid response format from server');
      }
      return MonthlySummaryModel.fromJson(data);
    } catch (e) {
      rethrow;
    }
  }

  Future<MonthlySummaryModel> update(
    int id, {
    int? month,
    int? year,
    int? totalIncome,
    int? totalExpense,
    int? balance,
  }) async {
    try {
      final requestData = <String, dynamic>{};
      if (month != null) requestData['month'] = month;
      if (year != null) requestData['year'] = year;
      if (totalIncome != null) requestData['total_income'] = totalIncome;
      if (totalExpense != null) requestData['total_expense'] = totalExpense;
      if (balance != null) requestData['balance'] = balance;

      final response = await _apiService.put(
        '${ApiConfig.monthlySummaryById}/$id',
        data: requestData,
      );
      final responseData = response.data['data'];
      if (responseData == null || responseData is! Map<String, dynamic>) {
        throw Exception('Invalid response format from server');
      }
      return MonthlySummaryModel.fromJson(responseData);
    } catch (e) {
      rethrow;
    }
  }

  Future<void> delete(int id) async {
    try {
      await _apiService.delete('${ApiConfig.monthlySummaryById}/$id');
    } catch (e) {
      rethrow;
    }
  }

  Future<MonthlySummaryModel> generate() async {
    try {
      final response = await _apiService.post(ApiConfig.monthlySummaryGenerate);
      if (response.data['success'] == true) {
        final data = response.data['data'];
      if (data == null || data is! Map<String, dynamic>) {
        throw Exception('Invalid response format from server');
      }
      return MonthlySummaryModel.fromJson(data);
      }
      throw Exception(response.data['message'] ?? 'Failed to generate summary');
    } catch (e) {
      rethrow;
    }
  }

  Future<String> downloadPDF(int id) async {
    try {
      final fileName = 'Financial_Summary_$id.pdf';
      return await _apiService.downloadFile(
        '${ApiConfig.monthlySummaryExportPdf}/$id/export-pdf',
        fileName,
      );
    } catch (e) {
      rethrow;
    }
  }
}




