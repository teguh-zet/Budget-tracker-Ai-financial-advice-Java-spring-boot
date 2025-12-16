import 'dart:io';
import 'package:dio/dio.dart' as dio;
import '../models/user_model.dart';
import '../services/api_service.dart';
import '../config/api_config.dart';

class UserService {
  final ApiService _apiService = ApiService();

  Future<List<UserModel>> getAll() async {
    try {
      final response = await _apiService.get(ApiConfig.users);
      final List<dynamic> data = response.data['data'] ?? [];
      return data
          .where((json) => json != null && json is Map<String, dynamic>)
          .map((json) => UserModel.fromJson(json as Map<String, dynamic>))
          .toList();
    } catch (e) {
      rethrow;
    }
  }

  Future<UserModel> getById(int id) async {
    try {
      final response = await _apiService.get('${ApiConfig.userById}/$id');
      final data = response.data['data'];
      if (data == null || data is! Map<String, dynamic>) {
        throw Exception('Invalid response format from server');
      }
      return UserModel.fromJson(data);
    } catch (e) {
      rethrow;
    }
  }

  Future<UserModel> create({
    required String name,
    required String email,
    required String password,
    String? number,
  }) async {
    try {
      final response = await _apiService.post(
        ApiConfig.users,
        data: {
          'name': name,
          'email': email,
          'password': password,
          'number': number,
        },
      );
      final data = response.data['data'];
      if (data == null || data is! Map<String, dynamic>) {
        throw Exception('Invalid response format from server');
      }
      return UserModel.fromJson(data);
    } catch (e) {
      rethrow;
    }
  }

  Future<UserModel> update(
    int id, {
    String? name,
    String? email,
    String? password,
    String? number,
  }) async {
    try {
      final data = <String, dynamic>{};
      if (name != null) data['name'] = name;
      if (email != null) data['email'] = email;
      if (password != null) data['password'] = password;
      if (number != null) data['number'] = number;

      final response = await _apiService.put(
        '${ApiConfig.userById}/$id',
        data: data,
      );
      final data = response.data['data'];
      if (data == null || data is! Map<String, dynamic>) {
        throw Exception('Invalid response format from server');
      }
      return UserModel.fromJson(data);
    } catch (e) {
      rethrow;
    }
  }

  Future<void> delete(int id) async {
    try {
      await _apiService.delete('${ApiConfig.userById}/$id');
    } catch (e) {
      rethrow;
    }
  }

  Future<UserModel> uploadProfilePicture(String filePath) async {
    try {
      final file = File(filePath);
      
      final formData = dio.FormData.fromMap({
        'file': await dio.MultipartFile.fromFile(
          filePath,
          filename: file.path.split('/').last,
        ),
      });

      final response = await _apiService.postFormData(
        ApiConfig.userProfilePicture,
        formData,
      );

      if (response.data['success'] == true) {
        final data = response.data['data'];
      if (data == null || data is! Map<String, dynamic>) {
        throw Exception('Invalid response format from server');
      }
      return UserModel.fromJson(data);
      }
      throw Exception(response.data['message'] ?? 'Failed to upload profile picture');
    } catch (e) {
      rethrow;
    }
  }

  Future<void> deleteProfilePicture() async {
    try {
      final response = await _apiService.delete(ApiConfig.userProfilePicture);
      if (response.data['success'] != true) {
        throw Exception(response.data['message'] ?? 'Failed to delete profile picture');
      }
    } catch (e) {
      rethrow;
    }
  }

  Future<String?> getProfilePictureUrl(int userId) async {
    try {
      return '${ApiConfig.baseUrl}${ApiConfig.userProfilePicture}?userId=$userId';
    } catch (e) {
      return null;
    }
  }
}




