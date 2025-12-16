import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:flutter_datetime_picker_plus/flutter_datetime_picker_plus.dart';
import '../../providers/transaction_provider.dart';
import '../../providers/category_provider.dart';
import '../../services/transaction_service.dart';
import '../../utils/format_rupiah.dart';
import '../../utils/date_formatter.dart';

class TransactionEditScreen extends StatefulWidget {
  final int transactionId;

  const TransactionEditScreen({super.key, required this.transactionId});

  @override
  State<TransactionEditScreen> createState() => _TransactionEditScreenState();
}

class _TransactionEditScreenState extends State<TransactionEditScreen> {
  final _formKey = GlobalKey<FormState>();
  String? _selectedType;
  final _amountController = TextEditingController();
  DateTime? _selectedDate;
  final _noteController = TextEditingController();
  int? _selectedCategoryId;
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _loadData();
  }

  Future<void> _loadData() async {
    try {
      // Load kategori dulu, baru load transaction
      final categoryProvider = Provider.of<CategoryProvider>(context, listen: false);
      await categoryProvider.fetchCategories();
      
      final transactionService = TransactionService();
      final transaction = await transactionService.getById(widget.transactionId);
      
      // Pastikan kategori sudah ter-load sebelum set categoryId
      final categories = transaction.type == 'income'
          ? categoryProvider.incomeCategories
          : categoryProvider.expenseCategories;
      
      // Validasi bahwa categoryId ada di list kategori
      final categoryExists = categories.any((cat) => cat.id == transaction.categoryId);
      
      setState(() {
        _selectedType = transaction.type;
        _amountController.text = FormatRupiah.formatWithoutSymbol(transaction.amount);
        _selectedDate = transaction.date;
        _noteController.text = transaction.note ?? '';
        // Hanya set categoryId jika ada di list kategori
        _selectedCategoryId = categoryExists ? transaction.categoryId : null;
        _isLoading = false;
      });
    } catch (e) {
      setState(() {
        _isLoading = false;
      });
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Error: $e')),
        );
      }
    }
  }

  @override
  void dispose() {
    _amountController.dispose();
    _noteController.dispose();
    super.dispose();
  }

  Future<void> _handleSubmit() async {
    if (!_formKey.currentState!.validate()) return;
    if (_selectedCategoryId == null || _selectedDate == null || _selectedType == null) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Lengkapi semua field')),
      );
      return;
    }

    final amount = FormatRupiah.parse(_amountController.text);

    final provider = Provider.of<TransactionProvider>(context, listen: false);
    final success = await provider.updateTransaction(
      widget.transactionId,
      type: _selectedType,
      amount: amount,
      date: _selectedDate,
      categoryId: _selectedCategoryId,
      note: _noteController.text.trim().isEmpty
          ? null
          : _noteController.text.trim(),
    );

    if (success && mounted) {
      Navigator.of(context).pop();
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Transaksi berhasil diupdate')),
      );
    } else if (mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(provider.error ?? 'Gagal update transaksi'),
          backgroundColor: Colors.red,
        ),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    final categoryProvider = Provider.of<CategoryProvider>(context);
    final transactionProvider = Provider.of<TransactionProvider>(context);

    if (_isLoading) {
      return const Scaffold(
        body: Center(child: CircularProgressIndicator()),
      );
    }

    final categories = _selectedType == 'income'
        ? categoryProvider.incomeCategories
        : categoryProvider.expenseCategories;

    return Scaffold(
      appBar: AppBar(
        title: const Text('Edit Transaksi'),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16.0),
        child: Form(
          key: _formKey,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              SegmentedButton<String>(
                segments: const [
                  ButtonSegment(
                    value: 'expense',
                    label: Text('Pengeluaran'),
                    icon: Icon(Icons.arrow_downward),
                  ),
                  ButtonSegment(
                    value: 'income',
                    label: Text('Pemasukan'),
                    icon: Icon(Icons.arrow_upward),
                  ),
                ],
                selected: _selectedType != null ? {_selectedType!} : {},
                onSelectionChanged: (Set<String> newSelection) {
                  setState(() {
                    _selectedType = newSelection.first;
                    _selectedCategoryId = null;
                  });
                },
              ),
              const SizedBox(height: 24),
              TextFormField(
                controller: _amountController,
                keyboardType: TextInputType.number,
                decoration: const InputDecoration(
                  labelText: 'Jumlah',
                  prefixIcon: Icon(Icons.attach_money),
                  border: OutlineInputBorder(),
                ),
                validator: (value) {
                  if (value == null || value.isEmpty) {
                    return 'Jumlah harus diisi';
                  }
                  final amount = FormatRupiah.parse(value);
                  if (amount <= 0) {
                    return 'Jumlah harus lebih dari 0';
                  }
                  return null;
                },
              ),
              const SizedBox(height: 16),
              ListTile(
                title: const Text('Tanggal'),
                subtitle: Text(_selectedDate != null
                    ? DateFormatter.formatDisplayDate(_selectedDate!)
                    : 'Pilih tanggal'),
                trailing: const Icon(Icons.calendar_today),
                onTap: () {
                  DatePicker.showDatePicker(
                    context,
                    showTitleActions: true,
                    minTime: DateTime(2020, 1, 1),
                    maxTime: DateTime.now(),
                    onConfirm: (date) {
                      setState(() {
                        _selectedDate = date;
                      });
                    },
                    currentTime: _selectedDate ?? DateTime.now(),
                  );
                },
              ),
              const SizedBox(height: 16),
              if (categoryProvider.isLoading) ...[
                const Center(child: CircularProgressIndicator()),
              ] else if (categories.isEmpty) ...[
                const Text(
                  'Tidak ada kategori untuk tipe ini.\nPastikan kategori sudah tersedia di sistem/backend.',
                  style: TextStyle(fontSize: 12, color: Colors.grey),
                ),
                const SizedBox(height: 8),
              ] else ...[
                DropdownButtonFormField<int>(
                  isExpanded: true,
                  value: _selectedCategoryId != null && 
                         categories.any((cat) => cat.id == _selectedCategoryId)
                      ? _selectedCategoryId
                      : null,
                  decoration: const InputDecoration(
                    labelText: 'Kategori',
                    prefixIcon: Icon(Icons.category),
                    border: OutlineInputBorder(),
                  ),
                  items: categories.map((category) {
                    return DropdownMenuItem<int>(
                      value: category.id,
                      child: Text(category.name),
                    );
                  }).toList(),
                  onChanged: (value) {
                    setState(() {
                      _selectedCategoryId = value;
                    });
                  },
                  validator: (value) {
                    if (value == null) {
                      return 'Pilih kategori';
                    }
                    return null;
                  },
                ),
              ],
              const SizedBox(height: 16),
              TextFormField(
                controller: _noteController,
                decoration: const InputDecoration(
                  labelText: 'Catatan (opsional)',
                  prefixIcon: Icon(Icons.note),
                  border: OutlineInputBorder(),
                ),
                maxLines: 3,
              ),
              const SizedBox(height: 32),
              ElevatedButton(
                onPressed: transactionProvider.isLoading ? null : _handleSubmit,
                style: ElevatedButton.styleFrom(
                  padding: const EdgeInsets.symmetric(vertical: 16),
                  backgroundColor: Colors.blue,
                ),
                child: transactionProvider.isLoading
                    ? const SizedBox(
                        height: 20,
                        width: 20,
                        child: CircularProgressIndicator(
                          strokeWidth: 2,
                          valueColor: AlwaysStoppedAnimation<Color>(Colors.white),
                        ),
                      )
                    : const Text(
                        'Update',
                        style: TextStyle(
                          fontSize: 16,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

