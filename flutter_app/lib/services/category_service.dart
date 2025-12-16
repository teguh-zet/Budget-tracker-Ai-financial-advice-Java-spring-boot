import '../models/category_model.dart';
import '../services/api_service.dart';
import '../config/api_config.dart';

class CategoryService {
  final ApiService _apiService = ApiService();

  Future<List<CategoryModel>> getAll() async {
    try {
      final response = await _apiService.get(ApiConfig.categories);
      final List<dynamic> data = response.data['data'] ?? [];
      return data
          .where((json) => json != null && json is Map<String, dynamic>)
          .map((json) => CategoryModel.fromJson(json as Map<String, dynamic>))
          .toList();
    } catch (e) {
      rethrow;
    }
  }

  Future<CategoryModel> getById(int id) async {
    try {
      final response = await _apiService.get('${ApiConfig.categoryById}/$id');
      final data = response.data['data'];
      if (data == null || data is! Map<String, dynamic>) {
        throw Exception('Invalid response format from server');
      }
      return CategoryModel.fromJson(data);
    } catch (e) {
      rethrow;
    }
  }

  Future<CategoryModel> create({
    required String name,
    required String type,
  }) async {
    try {
      final response = await _apiService.post(
        ApiConfig.categories,
        data: {
          'name': name,
          'type': type,
        },
      );
      final data = response.data['data'];
      if (data == null || data is! Map<String, dynamic>) {
        throw Exception('Invalid response format from server');
      }
      return CategoryModel.fromJson(data);
    } catch (e) {
      rethrow;
    }
  }

  Future<CategoryModel> update(
    int id, {
    String? name,
    String? type,
  }) async {
    try {
      final data = <String, dynamic>{};
      if (name != null) data['name'] = name;
      if (type != null) data['type'] = type;

      final response = await _apiService.put(
        '${ApiConfig.categoryById}/$id',
        data: data,
      );
      final data = response.data['data'];
      if (data == null || data is! Map<String, dynamic>) {
        throw Exception('Invalid response format from server');
      }
      return CategoryModel.fromJson(data);
    } catch (e) {
      rethrow;
    }
  }

  Future<void> delete(int id) async {
    try {
      await _apiService.delete('${ApiConfig.categoryById}/$id');
    } catch (e) {
      rethrow;
    }
  }
}




