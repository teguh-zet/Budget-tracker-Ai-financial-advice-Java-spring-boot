import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../providers/auth_provider.dart';
import '../../providers/transaction_provider.dart';
import '../../providers/category_provider.dart';
import '../../utils/format_rupiah.dart';
import '../../screens/transaction/transaction_list_screen.dart';
import '../../screens/transaction/transaction_create_screen.dart';
import '../../screens/monthly_summary/monthly_summary_screen.dart';
import '../../screens/profile/profile_screen.dart';

class DashboardScreen extends StatefulWidget {
  const DashboardScreen({super.key});

  @override
  State<DashboardScreen> createState() => _DashboardScreenState();
}

class _DashboardScreenState extends State<DashboardScreen> {
  int _selectedIndex = 0;

  @override
  void initState() {
    super.initState();
    _loadData();
  }

  Future<void> _loadData() async {
    final transactionProvider =
        Provider.of<TransactionProvider>(context, listen: false);
    final categoryProvider =
        Provider.of<CategoryProvider>(context, listen: false);

    await Future.wait([
      transactionProvider.fetchTransactions(),
      transactionProvider.fetchTodayTransactions(),
      transactionProvider.fetchMonthlySummary(),
      transactionProvider.fetchTodayExpenseStats(),
      categoryProvider.fetchCategories(),
    ]);
  }

  final List<Widget> _screens = [
    const DashboardHome(),
    const TransactionListScreen(),
    const MonthlySummaryScreen(),
    const ProfileScreen(),
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: _screens[_selectedIndex],
      bottomNavigationBar: BottomNavigationBar(
        currentIndex: _selectedIndex,
        onTap: (index) {
          setState(() {
            _selectedIndex = index;
          });
        },
        type: BottomNavigationBarType.fixed,
        items: const [
          BottomNavigationBarItem(
            icon: Icon(Icons.dashboard),
            label: 'Dashboard',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.receipt_long),
            label: 'Transaksi',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.summarize),
            label: 'Ringkasan',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.person),
            label: 'Profil',
          ),
        ],
      ),
      floatingActionButton: _selectedIndex == 1
          ? FloatingActionButton(
              onPressed: () {
                Navigator.of(context).push(
                  MaterialPageRoute(
                    builder: (_) => const TransactionCreateScreen(),
                  ),
                );
              },
              child: const Icon(Icons.add),
            )
          : null,
    );
  }
}

class DashboardHome extends StatelessWidget {
  const DashboardHome({super.key});

  @override
  Widget build(BuildContext context) {
    return Consumer2<TransactionProvider, AuthProvider>(
      builder: (context, transactionProvider, authProvider, child) {
        // monthlySummary berasal dari endpoint /api/v1/transactions/monthly-summary
        // Backend mengirim field: income, expense, balance, saving
        // BUKAN total_income / total_expense
        final monthlySummary = transactionProvider.monthlySummary;
        final todayStats = transactionProvider.todayExpenseStats;
        final todayTransactions = transactionProvider.todayTransactions;

        return RefreshIndicator(
          onRefresh: () async {
            await transactionProvider.fetchTransactions();
            await transactionProvider.fetchTodayTransactions();
            await transactionProvider.fetchMonthlySummary();
            await transactionProvider.fetchTodayExpenseStats();
          },
          child: SingleChildScrollView(
            padding: const EdgeInsets.all(16.0),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const SizedBox(height: 16),
                Text(
                  'Halo, ${authProvider.user?.name ?? "User"}!',
                  style: const TextStyle(
                    fontSize: 24,
                    fontWeight: FontWeight.bold,
                  ),
                ),
                const SizedBox(height: 8),
                const Text(
                  'Ringkasan keuangan Anda',
                  style: TextStyle(
                    fontSize: 16,
                    color: Colors.grey,
                  ),
                ),
                const SizedBox(height: 24),
                if (monthlySummary != null) ...[
                  _buildStatCard(
                    'Total Pemasukan',
                    // Back-end: income
                    FormatRupiah.format(monthlySummary['income'] ?? 0),
                    Colors.green,
                    Icons.arrow_upward,
                  ),
                  const SizedBox(height: 16),
                  _buildStatCard(
                    'Total Pengeluaran',
                    // Back-end: expense
                    FormatRupiah.format(monthlySummary['expense'] ?? 0),
                    Colors.red,
                    Icons.arrow_downward,
                  ),
                  const SizedBox(height: 16),
                  _buildStatCard(
                    'Saldo',
                    // Back-end: balance
                    FormatRupiah.format(monthlySummary['balance'] ?? 0),
                    Colors.blue,
                    Icons.account_balance_wallet,
                  ),
                  const SizedBox(height: 24),
                ],
                if (todayStats != null) ...[
                  const Text(
                    'Hari Ini',
                    style: TextStyle(
                      fontSize: 20,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  const SizedBox(height: 16),
                  _buildStatCard(
                    'Pengeluaran Hari Ini',
                    FormatRupiah.format(todayStats['total_expense'] ?? 0),
                    Colors.orange,
                    Icons.today,
                  ),
                  const SizedBox(height: 24),
                ],
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    const Text(
                      'Transaksi Hari Ini',
                      style: TextStyle(
                        fontSize: 20,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    TextButton(
                      onPressed: () {
                        Navigator.of(context).push(
                          MaterialPageRoute(
                            builder: (_) => const TransactionListScreen(),
                          ),
                        );
                      },
                      child: const Text('Lihat Semua'),
                    ),
                  ],
                ),
                const SizedBox(height: 8),
                if (transactionProvider.isLoading)
                  const Center(child: CircularProgressIndicator())
                else if (todayTransactions.isEmpty)
                  const Card(
                    child: Padding(
                      padding: EdgeInsets.all(16.0),
                      child: Text('Tidak ada transaksi hari ini'),
                    ),
                  )
                else
                  ...todayTransactions.take(5).map((transaction) => Card(
                        margin: const EdgeInsets.only(bottom: 8),
                        child: ListTile(
                          leading: CircleAvatar(
                            backgroundColor: transaction.type == 'income'
                                ? Colors.green
                                : Colors.red,
                            child: Icon(
                              transaction.type == 'income'
                                  ? Icons.arrow_upward
                                  : Icons.arrow_downward,
                              color: Colors.white,
                            ),
                          ),
                          title: Text(transaction.category?.name ?? '-'),
                          subtitle: Text(transaction.note ?? ''),
                          trailing: Text(
                            '${transaction.type == 'income' ? '+' : '-'}${FormatRupiah.format(transaction.amount)}',
                            style: TextStyle(
                              color: transaction.type == 'income'
                                  ? Colors.green
                                  : Colors.red,
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                        ),
                      )),
              ],
            ),
          ),
        );
      },
    );
  }

  Widget _buildStatCard(String title, String value, Color color, IconData icon) {
    return Card(
      elevation: 2,
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Row(
          children: [
            CircleAvatar(
              backgroundColor: color.withOpacity(0.1),
              child: Icon(icon, color: color),
            ),
            const SizedBox(width: 16),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    title,
                    style: const TextStyle(
                      fontSize: 14,
                      color: Colors.grey,
                    ),
                  ),
                  const SizedBox(height: 4),
                  Text(
                    value,
                    style: TextStyle(
                      fontSize: 20,
                      fontWeight: FontWeight.bold,
                      color: color,
                    ),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}




