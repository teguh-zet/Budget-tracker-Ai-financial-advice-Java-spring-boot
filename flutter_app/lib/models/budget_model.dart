class BudgetModel {
  final int? id;
  final int userId;
  final int categoryId;
  final String categoryName;
  final double amount;
  final double spent;
  final String month;
  final int year;
  final String? createdAt;
  final String? updatedAt;

  BudgetModel({
    this.id,
    required this.userId,
    required this.categoryId,
    required this.categoryName,
    required this.amount,
    required this.spent,
    required this.month,
    required this.year,
    this.createdAt,
    this.updatedAt,
  });

  factory BudgetModel.fromJson(Map<String, dynamic> json) {
    return BudgetModel(
      id: json['id'] is int ? json['id'] : (json['id'] != null ? int.tryParse(json['id'].toString()) : null),
      userId: json['user_id'] is int ? json['user_id'] : (json['userId'] is int ? json['userId'] : int.tryParse((json['user_id'] ?? json['userId'] ?? '0').toString()) ?? 0),
      categoryId: json['category_id'] is int ? json['category_id'] : (json['categoryId'] is int ? json['categoryId'] : int.tryParse((json['category_id'] ?? json['categoryId'] ?? '0').toString()) ?? 0),
      categoryName: json['category_name']?.toString() ?? json['categoryName']?.toString() ?? '',
      amount: (json['amount'] is num ? json['amount'].toDouble() : double.tryParse((json['amount'] ?? 0).toString()) ?? 0.0),
      spent: (json['spent'] is num ? json['spent'].toDouble() : double.tryParse((json['spent'] ?? 0).toString()) ?? 0.0),
      month: json['month']?.toString() ?? '',
      year: json['year'] is int ? json['year'] : int.tryParse((json['year'] ?? 0).toString()) ?? 0,
      createdAt: json['created_at']?.toString() ?? json['createdAt']?.toString(),
      updatedAt: json['updated_at']?.toString() ?? json['updatedAt']?.toString(),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'user_id': userId,
      'category_id': categoryId,
      'amount': amount,
      'month': month,
      'year': year,
    };
  }

  double get remaining => amount - spent;
  double get progressPercentage => amount > 0 ? (spent / amount * 100) : 0;
  bool get isExceeded => spent > amount;
}

