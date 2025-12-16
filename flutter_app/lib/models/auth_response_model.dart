import 'user_model.dart';

class AuthResponseModel {
  final bool success;
  final String message;
  final String? token;
  final UserModel? user;

  AuthResponseModel({
    required this.success,
    required this.message,
    this.token,
    this.user,
  });

  factory AuthResponseModel.fromJson(Map<String, dynamic> json) {
    // Backend mengirim response dengan format ApiResponse:
    // { success, message, data: { token, user: { ... } } }
    final data = json['data'] as Map<String, dynamic>?;

    return AuthResponseModel(
      success: (json['success'] as bool?) ?? false,
      message: (json['message'] as String?) ?? '',
      token: data != null ? data['token'] as String? : json['token'] as String?,
      user: data != null && data['user'] != null
          ? UserModel.fromJson(data['user'] as Map<String, dynamic>)
          : (json['user'] != null
              ? UserModel.fromJson(json['user'] as Map<String, dynamic>)
              : null),
    );
  }
}




