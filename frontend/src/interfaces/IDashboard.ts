export interface SummaryData {
  balance: number;
  saving: number;
  income: number;
  expense: number;
}

export interface Transaction {
  id: number;
  category: {
    name: string;
  };
  note?: string;
  date: string;
  type: "income" | "expense";
  amount: string;
}

export interface ChartPoint {
  date: string;
  income: number;
  expense: number;
}