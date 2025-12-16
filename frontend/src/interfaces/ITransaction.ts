export interface TransactionFormData {
  type: "income" | "expense";
  amount: string;
  date: string;
  note: string;
  categoryId: number | string;
}

export interface CategoryOption {
  id: number;
  name: string;
}