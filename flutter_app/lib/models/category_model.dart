class CategoryModel {
  final int id;
  final String name;
  final String type; // 'income' or 'expense'
  final DateTime createdAt;
  final DateTime updatedAt;

  CategoryModel({
    required this.id,
    required this.name,
    required this.type,
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

  factory CategoryModel.fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      throw ArgumentError('JSON cannot be null');
    }
    // Normalisasi type ke lowercase agar kompatibel dengan berbagai format dari backend
    final rawType = json['type']?.toString().toLowerCase();
    final normalizedType = rawType == 'income' ? 'income' : 'expense';

    return CategoryModel(
      id: _parseInt(json['id']),
      name: json['name']?.toString() ?? '',
      type: normalizedType,
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
      'name': name,
      'type': type,
      'created_at': createdAt.toIso8601String(),
      'updated_at': updatedAt.toIso8601String(),
    };
  }
}




