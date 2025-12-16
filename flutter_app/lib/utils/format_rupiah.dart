import 'package:intl/intl.dart';

class FormatRupiah {
  static String format(int amount) {
    final formatter = NumberFormat.currency(
      locale: 'id_ID',
      symbol: 'Rp ',
      decimalDigits: 0,
    );
    return formatter.format(amount);
  }
  
  static String formatWithoutSymbol(int amount) {
    final formatter = NumberFormat.currency(
      locale: 'id_ID',
      symbol: '',
      decimalDigits: 0,
    );
    return formatter.format(amount);
  }
  
  static int parse(String rupiahString) {
    // Remove "Rp " and dots, then parse
    String cleaned = rupiahString
        .replaceAll('Rp ', '')
        .replaceAll('.', '')
        .trim();
    return int.tryParse(cleaned) ?? 0;
  }
}




