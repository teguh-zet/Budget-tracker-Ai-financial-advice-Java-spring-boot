import 'package:intl/intl.dart';

class DateFormatter {
  static String formatDate(DateTime date) {
    return DateFormat('yyyy-MM-dd').format(date);
  }
  
  static String formatDateTime(DateTime date) {
    return DateFormat('yyyy-MM-dd HH:mm:ss').format(date);
  }
  
  static String formatDisplayDate(DateTime date) {
    return DateFormat('dd MMMM yyyy', 'id_ID').format(date);
  }
  
  static String formatDisplayDateTime(DateTime date) {
    return DateFormat('dd MMMM yyyy HH:mm', 'id_ID').format(date);
  }
  
  static DateTime? parseDate(String dateString) {
    try {
      return DateFormat('yyyy-MM-dd').parse(dateString);
    } catch (e) {
      return null;
    }
  }
  
  static String getMonthYear(DateTime date) {
    return DateFormat('MMMM yyyy', 'id_ID').format(date);
  }
}




