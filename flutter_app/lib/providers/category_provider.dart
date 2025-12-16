import 'package:flutter/foundation.dart';
import '../models/category_model.dart';
import '../services/category_service.dart';

class CategoryProvider with ChangeNotifier {
  final CategoryService _categoryService = CategoryService();
  
  List<CategoryModel> _categories = [];
  bool _isLoading = false;
  String? _error;

  List<CategoryModel> get categories => _categories;
  List<CategoryModel> get incomeCategories => 
      _categories.where((c) => c.type == 'income').toList();
  List<CategoryModel> get expenseCategories => 
      _categories.where((c) => c.type == 'expense').toList();
  bool get isLoading => _isLoading;
  String? get error => _error;

  Future<void> fetchCategories() async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      _categories = await _categoryService.getAll();
      _error = null;
    } catch (e) {
      _error = e.toString();
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  Future<bool> createCategory({
    required String name,
    required String type,
  }) async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      await _categoryService.create(name: name, type: type);
      await fetchCategories();
      _error = null;
      return true;
    } catch (e) {
      _error = e.toString();
      return false;
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  Future<bool> updateCategory(
    int id, {
    String? name,
    String? type,
  }) async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      await _categoryService.update(id, name: name, type: type);
      await fetchCategories();
      _error = null;
      return true;
    } catch (e) {
      _error = e.toString();
      return false;
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  Future<bool> deleteCategory(int id) async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      await _categoryService.delete(id);
      await fetchCategories();
      _error = null;
      return true;
    } catch (e) {
      _error = e.toString();
      return false;
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }
}




