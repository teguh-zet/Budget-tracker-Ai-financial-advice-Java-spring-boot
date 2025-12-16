class MonthlySummaryModel {
  final int id;
  final int userId;
  final int month;
  final int year;
  final int totalIncome;
  final int totalExpense;
  final int balance;
  final String? aiSummary;
  final String? aiRecommendation;
  final DateTime createdAt;
  final DateTime updatedAt;

  MonthlySummaryModel({
    required this.id,
    required this.userId,
    required this.month,
    required this.year,
    required this.totalIncome,
    required this.totalExpense,
    required this.balance,
    this.aiSummary,
    this.aiRecommendation,
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

  factory MonthlySummaryModel.fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      throw ArgumentError('JSON cannot be null');
    }
    return MonthlySummaryModel(
      id: _parseInt(json['id']),
      userId: _parseInt(json['user_id']),
      month: _parseInt(json['month']),
      year: _parseInt(json['year']),
      totalIncome: _parseInt(json['total_income']),
      totalExpense: _parseInt(json['total_expense']),
      balance: _parseInt(json['balance']),
      aiSummary: json['ai_summary']?.toString(),
      aiRecommendation: json['ai_recommendation']?.toString(),
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
      'user_id': userId,
      'month': month,
      'year': year,
      'total_income': totalIncome,
      'total_expense': totalExpense,
      'balance': balance,
      'ai_summary': aiSummary,
      'ai_recommendation': aiRecommendation,
      'created_at': createdAt.toIso8601String(),
      'updated_at': updatedAt.toIso8601String(),
    };
  }
}




