import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:flutter_datetime_picker_plus/flutter_datetime_picker_plus.dart';
import '../../providers/transaction_provider.dart';
import '../../providers/category_provider.dart';
import '../../utils/format_rupiah.dart';
import '../../utils/date_formatter.dart';

class TransactionCreateScreen extends StatefulWidget {
  const TransactionCreateScreen({super.key});

  @override
  State<TransactionCreateScreen> createState() => _TransactionCreateScreenState();
}

class _TransactionCreateScreenState extends State<TransactionCreateScreen> {
  final _formKey = GlobalKey<FormState>();
  String _selectedType = 'expense';
  final _amountController = TextEditingController();
  DateTime _selectedDate = DateTime.now();
  final _noteController = TextEditingController();
  int? _selectedCategoryId;

  @override
  void initState() {
    super.initState();
    Provider.of<CategoryProvider>(context, listen: false).fetchCategories();
  }

  @override
  void dispose() {
    _amountController.dispose();
    _noteController.dispose();
    super.dispose();
  }

  Future<void> _handleSubmit() async {
    if (!_formKey.currentState!.validate()) return;
    if (_selectedCategoryId == null) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Pilih kategori terlebih dahulu')),
      );
      return;
    }

    final amount = FormatRupiah.parse(_amountController.text);

    final provider = Provider.of<TransactionProvider>(context, listen: false);
    final success = await provider.createTransaction(
      type: _selectedType,
      amount: amount,
      date: _selectedDate,
      categoryId: _selectedCategoryId!,
      note: _noteController.text.trim().isEmpty
          ? null
          : _noteController.text.trim(),
    );

    if (success && mounted) {
      Navigator.of(context).pop();
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Transaksi berhasil dibuat')),
      );
    } else if (mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(provider.error ?? 'Gagal membuat transaksi'),
          backgroundColor: Colors.red,
        ),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    final categoryProvider = Provider.of<CategoryProvider>(context);
    final transactionProvider = Provider.of<TransactionProvider>(context);

    final categories = _selectedType == 'income'
        ? categoryProvider.incomeCategories
        : categoryProvider.expenseCategories;

    return Scaffold(
      appBar: AppBar(
        title: const Text('Tambah Transaksi'),
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
                selected: {_selectedType},
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
                  hintText: 'Rp 0',
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
                onChanged: (value) {
                  // Format rupiah saat mengetik
                  if (value.isNotEmpty) {
                    final amount = FormatRupiah.parse(value);
                    if (amount > 0) {
                      final formatted = FormatRupiah.formatWithoutSymbol(amount);
                      if (value != formatted) {
                        _amountController.value = TextEditingValue(
                          text: formatted,
                          selection: TextSelection.collapsed(offset: formatted.length),
                        );
                      }
                    }
                  }
                },
              ),
              const SizedBox(height: 16),
              ListTile(
                title: const Text('Tanggal'),
                subtitle: Text(DateFormatter.formatDisplayDate(_selectedDate)),
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
                    currentTime: _selectedDate,
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
                  value: _selectedCategoryId,
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
                        'Simpan',
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




