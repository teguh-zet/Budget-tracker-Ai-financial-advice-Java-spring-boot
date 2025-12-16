class FinancialGoalModel {
  final int? id;
  final int userId;
  final String name;
  final String? description;
  final double targetAmount;
  final double currentAmount;
  final String deadline;
  final String type;
  final String status;
  final String? icon;
  final double progressPercentage;
  final double remainingAmount;
  final int daysRemaining;
  final bool isCompleted;
  final bool isOverdue;
  final String? createdAt;
  final String? updatedAt;

  FinancialGoalModel({
    this.id,
    required this.userId,
    required this.name,
    this.description,
    required this.targetAmount,
    required this.currentAmount,
    required this.deadline,
    required this.type,
    required this.status,
    this.icon,
    required this.progressPercentage,
    required this.remainingAmount,
    required this.daysRemaining,
    required this.isCompleted,
    required this.isOverdue,
    this.createdAt,
    this.updatedAt,
  });

  factory FinancialGoalModel.fromJson(Map<String, dynamic> json) {
    return FinancialGoalModel(
      id: json['id'] is int ? json['id'] : (json['id'] != null ? int.tryParse(json['id'].toString()) : null),
      userId: json['user_id'] is int ? json['user_id'] : (json['userId'] is int ? json['userId'] : int.tryParse((json['user_id'] ?? json['userId'] ?? '0').toString()) ?? 0),
      name: json['name']?.toString() ?? '',
      description: json['description']?.toString(),
      targetAmount: (json['target_amount'] is num ? json['target_amount'].toDouble() : (json['targetAmount'] is num ? json['targetAmount'].toDouble() : double.tryParse((json['target_amount'] ?? json['targetAmount'] ?? 0).toString()) ?? 0.0)),
      currentAmount: (json['current_amount'] is num ? json['current_amount'].toDouble() : (json['currentAmount'] is num ? json['currentAmount'].toDouble() : double.tryParse((json['current_amount'] ?? json['currentAmount'] ?? 0).toString()) ?? 0.0)),
      deadline: json['deadline']?.toString() ?? '',
      type: json['type']?.toString() ?? '',
      status: json['status']?.toString() ?? 'ACTIVE',
      icon: json['icon']?.toString(),
      progressPercentage: (json['progress_percentage'] is num ? json['progress_percentage'].toDouble() : (json['progressPercentage'] is num ? json['progressPercentage'].toDouble() : double.tryParse((json['progress_percentage'] ?? json['progressPercentage'] ?? 0).toString()) ?? 0.0)),
      remainingAmount: (json['remaining_amount'] is num ? json['remaining_amount'].toDouble() : (json['remainingAmount'] is num ? json['remainingAmount'].toDouble() : double.tryParse((json['remaining_amount'] ?? json['remainingAmount'] ?? 0).toString()) ?? 0.0)),
      daysRemaining: json['days_remaining'] is int ? json['days_remaining'] : (json['daysRemaining'] is int ? json['daysRemaining'] : int.tryParse((json['days_remaining'] ?? json['daysRemaining'] ?? 0).toString()) ?? 0),
      isCompleted: json['is_completed'] is bool ? json['is_completed'] : (json['isCompleted'] is bool ? json['isCompleted'] : (json['is_completed'] ?? json['isCompleted'] ?? false) == true),
      isOverdue: json['is_overdue'] is bool ? json['is_overdue'] : (json['isOverdue'] is bool ? json['isOverdue'] : (json['is_overdue'] ?? json['isOverdue'] ?? false) == true),
      createdAt: json['created_at']?.toString() ?? json['createdAt']?.toString(),
      updatedAt: json['updated_at']?.toString() ?? json['updatedAt']?.toString(),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      'description': description,
      'target_amount': targetAmount,
      'deadline': deadline,
      'type': type,
      'icon': icon,
    };
  }

  Map<String, dynamic> toUpdateJson() {
    return {
      if (name.isNotEmpty) 'name': name,
      if (description != null) 'description': description,
      if (targetAmount > 0) 'target_amount': targetAmount,
      if (deadline.isNotEmpty) 'deadline': deadline,
      if (type.isNotEmpty) 'type': type,
      if (status.isNotEmpty) 'status': status,
      if (icon != null) 'icon': icon,
    };
  }
}

