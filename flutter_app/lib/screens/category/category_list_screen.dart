import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../providers/category_provider.dart';
import '../../models/category_model.dart';

class CategoryListScreen extends StatefulWidget {
  const CategoryListScreen({super.key});

  @override
  State<CategoryListScreen> createState() => _CategoryListScreenState();
}

class _CategoryListScreenState extends State<CategoryListScreen> {
  @override
  void initState() {
    super.initState();
    _loadCategories();
  }

  Future<void> _loadCategories() async {
    final provider = Provider.of<CategoryProvider>(context, listen: false);
    await provider.fetchCategories();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Kategori'),
      ),
      body: Consumer<CategoryProvider>(
        builder: (context, provider, child) {
          if (provider.isLoading) {
            return const Center(child: CircularProgressIndicator());
          }

          if (provider.error != null) {
            return Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Text('Error: ${provider.error}'),
                  ElevatedButton(
                    onPressed: _loadCategories,
                    child: const Text('Coba Lagi'),
                  ),
                ],
              ),
            );
          }

          if (provider.categories.isEmpty) {
            return const Center(
              child: Text('Tidak ada kategori'),
            );
          }

          return RefreshIndicator(
            onRefresh: _loadCategories,
            child: ListView(
              children: [
                const Padding(
                  padding: EdgeInsets.all(16.0),
                  child: Text(
                    'Pemasukan',
                    style: TextStyle(
                      fontSize: 18,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ),
                ...provider.incomeCategories.map((category) =>
                    _buildCategoryCard(category)),
                const Padding(
                  padding: EdgeInsets.all(16.0),
                  child: Text(
                    'Pengeluaran',
                    style: TextStyle(
                      fontSize: 18,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ),
                ...provider.expenseCategories.map((category) =>
                    _buildCategoryCard(category)),
              ],
            ),
          );
        },
      ),
    );
  }

  Widget _buildCategoryCard(CategoryModel category) {
    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      child: ListTile(
        leading: CircleAvatar(
          backgroundColor: category.type == 'income' ? Colors.green : Colors.red,
          child: Icon(
            category.type == 'income' ? Icons.arrow_upward : Icons.arrow_downward,
            color: Colors.white,
          ),
        ),
        title: Text(category.name),
        subtitle: Text(category.type == 'income' ? 'Pemasukan' : 'Pengeluaran'),
      ),
    );
  }
}




