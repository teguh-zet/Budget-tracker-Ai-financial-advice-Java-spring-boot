import 'dart:io';
import 'package:dio/dio.dart';
import 'package:path_provider/path_provider.dart';
import '../config/api_config.dart';
import '../utils/storage.dart';

class ApiService {
  late Dio _dio;

  ApiService() {
    _dio = Dio(
      BaseOptions(
        baseUrl: ApiConfig.baseUrl,
        connectTimeout: Duration(seconds: ApiConfig.timeout),
        receiveTimeout: Duration(seconds: ApiConfig.timeout),
        headers: {
          'Content-Type': 'application/json',
        },
      ),
    );

    // Interceptor untuk menambahkan token
    _dio.interceptors.add(
      InterceptorsWrapper(
        onRequest: (options, handler) async {
          final token = await Storage.getToken();
          if (token != null) {
            options.headers['Authorization'] = 'Bearer $token';
          }
          return handler.next(options);
        },
        onError: (error, handler) {
          if (error.response?.statusCode == 401) {
            // Token expired atau tidak valid
            Storage.removeToken();
          }
          return handler.next(error);
        },
      ),
    );
  }

  // GET request
  Future<Response> get(String path, {Map<String, dynamic>? queryParameters}) async {
    try {
      return await _dio.get(path, queryParameters: queryParameters);
    } catch (e) {
      rethrow;
    }
  }

  // POST request
  Future<Response> post(String path, {dynamic data}) async {
    try {
      return await _dio.post(path, data: data);
    } catch (e) {
      rethrow;
    }
  }

  // PUT request
  Future<Response> put(String path, {dynamic data}) async {
    try {
      return await _dio.put(path, data: data);
    } catch (e) {
      rethrow;
    }
  }

  // DELETE request
  Future<Response> delete(String path) async {
    try {
      return await _dio.delete(path);
    } catch (e) {
      rethrow;
    }
  }

  // POST with FormData (for file upload)
  Future<Response> postFormData(String path, FormData formData) async {
    try {
      final token = await Storage.getToken();
      return await _dio.post(
        path,
        data: formData,
        options: Options(
          headers: {
            if (token != null) 'Authorization': 'Bearer $token',
            'Content-Type': 'multipart/form-data',
          },
        ),
      );
    } catch (e) {
      rethrow;
    }
  }

  // Download file
  Future<String> downloadFile(String path, String fileName) async {
    try {
      final token = await Storage.getToken();
      final response = await _dio.get(
        path,
        options: Options(
          responseType: ResponseType.bytes,
          headers: {
            'Authorization': token != null ? 'Bearer $token' : null,
          },
        ),
      );

      final directory = await getApplicationDocumentsDirectory();
      final filePath = '${directory.path}/$fileName';
      final file = File(filePath);
      await file.writeAsBytes(response.data);
      
      return filePath;
    } catch (e) {
      rethrow;
    }
  }

  // Get Dio instance (for advanced usage)
  Dio get dio => _dio;
}




