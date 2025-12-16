class ApiConfig {
  // Base URL untuk development
  // Untuk Android Emulator: gunakan 'http://10.0.2.2:5001/api/v1'
  // Untuk iOS Simulator: gunakan 'http://localhost:5001/api/v1'
  // Untuk Device Fisik: gunakan 'http://<IP_KOMPUTER>:5001/api/v1'
  static const String baseUrl = 'http://10.0.2.2:5001/api/v1'; // Default untuk Android Emulator
  
  // Timeout untuk request (dalam detik)
  static const int timeout = 30;
  
  // Endpoints
  static const String authRegister = '/auth/register';
  static const String authLogin = '/auth/login';
  static const String authProfile = '/auth/profile';
  
  static const String transactions = '/transaction';
  static const String transactionById = '/transaction';
  static const String transactionMonthlySummary = '/transaction/monthly-summary';
  static const String transactionMonthlyChart = '/transaction/monthly-chart';
  static const String transactionToday = '/transaction/today';
  static const String transactionTodayExpense = '/transaction/today-expense-stats';
  
  static const String categories = '/category';
  static const String categoryById = '/category';
  
  static const String monthlySummaries = '/monthly-summary';
  static const String monthlySummaryById = '/monthly-summary';
  static const String monthlySummaryGenerate = '/monthly-summary/generate';
  
  static const String users = '/users';
  static const String userById = '/users';
  static const String userProfilePicture = '/users/profile/picture';
  
  static const String budgets = '/budget';
  static const String budgetsActive = '/budget/active';
  static const String budgetById = '/budget';
  
  static const String financialGoals = '/financial-goals';
  static const String financialGoalsActive = '/financial-goals/active';
  static const String financialGoalsCompleted = '/financial-goals/completed';
  static const String financialGoalById = '/financial-goals';
  
  static const String monthlySummaryExportPdf = '/monthly-summary';
  
  static const String health = '/health';
  static const String dbPing = '/db-ping';
}




