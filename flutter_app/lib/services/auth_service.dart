import '../models/auth_response_model.dart';
import '../models/user_model.dart';
import '../services/api_service.dart';
import '../utils/storage.dart';
import '../config/api_config.dart';

class AuthService {
  final ApiService _apiService = ApiService();

  Future<AuthResponseModel> register({
    required String name,
    required String email,
    required String password,
    required String number,
  }) async {
    try {
      final response = await _apiService.post(
        ApiConfig.authRegister,
        data: {
          'name': name,
          'email': email,
          'password': password,
          'number': number,
        },
      );

      final authResponse = AuthResponseModel.fromJson(response.data);
      
      if (authResponse.token != null) {
        await Storage.saveToken(authResponse.token!);
      }

      return authResponse;
    } catch (e) {
      rethrow;
    }
  }

  Future<AuthResponseModel> login({
    required String email,
    required String password,
  }) async {
    try {
      final response = await _apiService.post(
        ApiConfig.authLogin,
        data: {
          'email': email,
          'password': password,
        },
      );

      final authResponse = AuthResponseModel.fromJson(response.data);
      
      if (authResponse.token != null) {
        await Storage.saveToken(authResponse.token!);
      }

      return authResponse;
    } catch (e) {
      rethrow;
    }
  }

  Future<UserModel> getProfile() async {
    try {
      final response = await _apiService.get(ApiConfig.authProfile);
      final data = response.data['data'];
      if (data == null || data is! Map<String, dynamic>) {
        throw Exception('Invalid response format from server');
      }
      return UserModel.fromJson(data);
    } catch (e) {
      rethrow;
    }
  }

  Future<void> logout() async {
    await Storage.removeToken();
  }

  Future<bool> isLoggedIn() async {
    final token = await Storage.getToken();
    return token != null;
  }
}




