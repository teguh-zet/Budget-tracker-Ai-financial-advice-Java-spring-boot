import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../providers/monthly_summary_provider.dart';
import '../../utils/format_rupiah.dart';

class MonthlySummaryScreen extends StatefulWidget {
  const MonthlySummaryScreen({super.key});

  @override
  State<MonthlySummaryScreen> createState() => _MonthlySummaryScreenState();
}

class _MonthlySummaryScreenState extends State<MonthlySummaryScreen> {
  @override
  void initState() {
    super.initState();
    _loadSummaries();
  }

  Future<void> _loadSummaries() async {
    final provider = Provider.of<MonthlySummaryProvider>(context, listen: false);
    await provider.fetchMonthlySummaries();
  }

  Future<void> _handleGenerate() async {
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Generate Ringkasan Bulanan'),
        content: const Text(
          'Apakah Anda yakin ingin membuat ringkasan bulanan? Proses ini mungkin memakan waktu beberapa saat.',
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.of(context).pop(false),
            child: const Text('Batal'),
          ),
          TextButton(
            onPressed: () => Navigator.of(context).pop(true),
            child: const Text('Generate'),
          ),
        ],
      ),
    );

    if (confirmed == true) {
      final provider = Provider.of<MonthlySummaryProvider>(context, listen: false);
      final success = await provider.generateMonthlySummary();
      if (success && mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Ringkasan bulanan berhasil dibuat')),
        );
      } else if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text(provider.error ?? 'Gagal membuat ringkasan'),
            backgroundColor: Colors.red,
          ),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Ringkasan Bulanan'),
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: _handleGenerate,
            tooltip: 'Generate Ringkasan',
          ),
        ],
      ),
      body: Consumer<MonthlySummaryProvider>(
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
                    onPressed: _loadSummaries,
                    child: const Text('Coba Lagi'),
                  ),
                ],
              ),
            );
          }

          if (provider.monthlySummaries.isEmpty) {
            return Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  const Text('Tidak ada ringkasan bulanan'),
                  const SizedBox(height: 16),
                  ElevatedButton(
                    onPressed: _handleGenerate,
                    child: const Text('Generate Ringkasan'),
                  ),
                ],
              ),
            );
          }

          return RefreshIndicator(
            onRefresh: _loadSummaries,
            child: ListView.builder(
              itemCount: provider.monthlySummaries.length,
              itemBuilder: (context, index) {
                final summary = provider.monthlySummaries[index];
                return Card(
                  margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                  child: Padding(
                    padding: const EdgeInsets.all(16.0),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          '${_getMonthName(summary.month)} ${summary.year}',
                          style: const TextStyle(
                            fontSize: 20,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                        const SizedBox(height: 16),
                        Row(
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            _buildSummaryItem(
                              'Pemasukan',
                              FormatRupiah.format(summary.totalIncome),
                              Colors.green,
                            ),
                            _buildSummaryItem(
                              'Pengeluaran',
                              FormatRupiah.format(summary.totalExpense),
                              Colors.red,
                            ),
                            _buildSummaryItem(
                              'Saldo',
                              FormatRupiah.format(summary.balance),
                              Colors.blue,
                            ),
                          ],
                        ),
                        if (summary.aiSummary != null) ...[
                          const SizedBox(height: 16),
                          const Divider(),
                          const SizedBox(height: 8),
                          const Text(
                            'Ringkasan AI:',
                            style: TextStyle(
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                          const SizedBox(height: 8),
                          Text(summary.aiSummary!),
                        ],
                        if (summary.aiRecommendation != null) ...[
                          const SizedBox(height: 16),
                          const Text(
                            'Rekomendasi AI:',
                            style: TextStyle(
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                          const SizedBox(height: 8),
                          Text(summary.aiRecommendation!),
                        ],
                      ],
                    ),
                  ),
                );
              },
            ),
          );
        },
      ),
    );
  }

  Widget _buildSummaryItem(String label, String value, Color color) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          label,
          style: const TextStyle(
            fontSize: 12,
            color: Colors.grey,
          ),
        ),
        const SizedBox(height: 4),
        Text(
          value,
          style: TextStyle(
            fontSize: 16,
            fontWeight: FontWeight.bold,
            color: color,
          ),
        ),
      ],
    );
  }

  String _getMonthName(int month) {
    const months = [
      'Januari',
      'Februari',
      'Maret',
      'April',
      'Mei',
      'Juni',
      'Juli',
      'Agustus',
      'September',
      'Oktober',
      'November',
      'Desember',
    ];
    return months[month - 1];
  }
}




