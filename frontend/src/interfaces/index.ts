import { TransactionFormData } from "@/interfaces/ITransaction";

export interface TransaksiProps {
  initialData?: {
    type: "income" | "expense";
    amount: string;
    date: string;
    note: string;
    categoryId: number | string;
  };
  onSubmit: (data: TransactionFormData) => void;
}
