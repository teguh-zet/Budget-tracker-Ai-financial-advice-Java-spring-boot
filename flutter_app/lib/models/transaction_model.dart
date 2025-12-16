import 'category_model.dart';

class TransactionModel {
  final int id;
  final String type; // 'income' or 'expense'
  final int amount;
  final DateTime date;
  final String? note;
  final int userId;
  final int categoryId;
  final CategoryModel? category;
  final DateTime createdAt;
  final DateTime updatedAt;

  TransactionModel({
    required this.id,
    required this.type,
    required this.amount,
    required this.date,
    this.note,
    required this.userId,
    required this.categoryId,
    this.category,
    required this.createdAt,
    required this.updatedAt,
  });

  static int _parseInt(dynamic value, {int defaultValue = 0}) {
    if (value == null) return defaultValue;
    if (value is int) return value;
    final str = value.toString();
    final parsed = int.tryParse(str);
    return parsed ?? defaultValue;
  }

  factory TransactionModel.fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      throw ArgumentError('JSON cannot be null');
    }
    return TransactionModel(
      id: _parseInt(json['id']),
      type: json['type']?.toString() ?? 'expense',
      amount: _parseInt(json['amount']),
      date: _parseDateTime(json['date']),
      note: json['note']?.toString(),
      userId: _parseInt(json['user_id']),
      categoryId: _parseInt(json['category_id']),
      category: json['category'] != null && json['category'] is Map<String, dynamic>
          ? CategoryModel.fromJson(json['category'] as Map<String, dynamic>)
          : null,
      createdAt: _parseDateTime(json['created_at']),
      updatedAt: _parseDateTime(json['updated_at']),
    );
  }

  static DateTime _parseDateTime(dynamic value) {
    if (value == null) return DateTime.now();
    if (value is DateTime) return value;
    try {
      return DateTime.parse(value.toString());
    } catch (e) {
      return DateTime.now();
    }
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'type': type,
      'amount': amount,
      'date': date.toIso8601String().split('T')[0],
      'note': note,
      'user_id': userId,
      'category_id': categoryId,
      'created_at': createdAt.toIso8601String(),
      'updated_at': updatedAt.toIso8601String(),
    };
  }
}




